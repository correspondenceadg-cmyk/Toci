use jni::objects::{JClass, JIntArray, JLongArray};
use jni::sys::jint;
use jni::JNIEnv;

mod orchestrator;
mod verification;
mod logger;

pub mod ffi {
    #[repr(C)]
    pub struct TocResult {
        pub method_used: u32,
        pub err: i32,
        pub bytes_wiped: u64,
    }

    extern "C" {
        pub fn toci_get_device_size(fd: i32, out: *mut u64) -> i32;
        pub fn toci_sanitize(fd: i32, pref: i32, r: *mut TocResult) -> i32;
        pub fn toci_bootpart_wipe(fd: i32, r: *mut TocResult) -> i32;
        pub fn toci_wearout_chunk(fd: i32, seconds: i32) -> i32;
        pub fn toci_rpmb_wipe(fd: i32) -> i32;
        pub fn toci_verify_readback(fd: i32, lbas: *const u64, count: u32, expected: *const *const u8, out_pass: *mut u32, out_fail: *mut u32) -> i32;
    }
}

// JNI

#[no_mangle]
pub extern "system" fn Java_ws_tilda_sentryprotocol_toci_Native_wipeAll(
    env: JNIEnv,
    _class: JClass,
    fds: JIntArray,
    _labels: JLongArray,
) -> jint {
    crate::logger::init();
    let n = match env.get_array_length(&fds) { Ok(x) => x as usize, Err(_) => return -1 };
    let mut buf = vec![0i32; n];
    if env.get_int_array_region(&fds, 0, &mut buf).is_err() { return -1; }

    let targets: Vec<orchestrator::Target> = buf
        .into_iter()
        .enumerate()
        .map(|(i, fd)| orchestrator::Target { fd, name: format!("fd[{i}]") })
        .collect();

    let report = orchestrator::run_wipe(&targets);
    log::info!("wipe: {} targets, {} passed, {} failed",
               report.results.len(), report.passed(), report.failed());
    report.failed() as jint
}

#[no_mangle]
pub extern "system" fn Java_ws_tilda_sentryprotocol_toci_Native_wearoutChunk(
    _env: JNIEnv, _class: JClass, fd: jint, seconds: jint
) -> jint {
    unsafe { crate::ffi::toci_wearout_chunk(fd, seconds) as jint }
}

#[no_mangle]
pub extern "system" fn Java_ws_tilda_sentryprotocol_toci_Native_wipeRpmb(
    _env: JNIEnv, _class: JClass, fd: jint
) -> jint {
    unsafe { crate::ffi::toci_rpmb_wipe(fd) as jint }
}

#[no_mangle]
pub extern "system" fn Java_ws_tilda_sentryprotocol_toci_Native_getVersion(
    _env: JNIEnv, _class: JClass) -> jint {
    1
}
