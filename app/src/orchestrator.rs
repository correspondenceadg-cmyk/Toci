use crate::ffi::{toci_sanitize, TocResult};

// Types

pub struct Target {
    pub fd: i32,
    pub name: String,
}

#[allow(dead_code)]
pub struct PartResult {
    pub name: String,
    pub method: u32,
    pub err: i32,
    pub bytes: u64,
}

use crate::verification::sample_size;

pub struct Report {
    pub results: Vec<PartResult>,
}

impl Report {
    pub fn passed(&self) -> i32 { self.results.iter().filter(|r| r.err == 0).count() as i32 }
    pub fn failed(&self) -> i32 { self.results.iter().filter(|r| r.err != 0).count() as i32 }
}

// Ladder

pub fn run_wipe(targets: &[Target]) -> Report {
    let mut results = Vec::with_capacity(targets.len());
    for t in targets {
        let mut r = TocResult { method_used: 0, err: 0, bytes_wiped: 0 };
        let rc = unsafe { toci_sanitize(t.fd, 0, &mut r) };
        if rc < 0 {
            log::warn!("sanitize failed for {}: errno {}", t.name, r.err);
        }
        if r.err == 0 && r.method_used != 0 {
            let count = sample_size(0.99, 0.01);
            let mut lbas = vec![0u64; count as usize];
            for i in 0..count {
                lbas[i as usize] = i as u64;
            }
            let expected = vec![std::ptr::null::<u8>(); count as usize];
            let mut pass = 0;
            let mut fail = 0;
            unsafe {
                crate::ffi::toci_verify_readback(
                    t.fd, lbas.as_ptr(), count, expected.as_ptr(), &mut pass, &mut fail
                );
            }
            if fail > 0 {
                log::warn!("verification failed on {}: {} blocks not zeroed", t.name, fail);
            }
        }
        results.push(PartResult {
            name: t.name.clone(),
            method: r.method_used,
            err: r.err,
            bytes: r.bytes_wiped,
        });
    }
    Report { results }
}
