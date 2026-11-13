use crate::ffi::{toci_sanitize, TocResult};

// Types

pub struct Target {
    pub fd: i32,
    pub name: String,
}

pub struct PartResult {
    pub name: String,
    pub method: u32,
    pub err: i32,
    pub bytes: u64,
}

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
        results.push(PartResult {
            name: t.name.clone(),
            method: r.method_used,
            err: r.err,
            bytes: r.bytes_wiped,
        });
    }
    Report { results }
}
