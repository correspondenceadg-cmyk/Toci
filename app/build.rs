use std::path::PathBuf;

fn main() {
    let src_dir = PathBuf::from(env!("CARGO_MANIFEST_DIR"))
        .join("..").join("firmware").join("src");
    let inc_dir = PathBuf::from(env!("CARGO_MANIFEST_DIR"))
        .join("..").join("firmware").join("include");

    let mut build = cc::Build::new();
    for entry in std::fs::read_dir(&src_dir).expect("read firmware/src") {
        let path = entry.expect("dir entry").path();
        if path.extension().map_or(false, |e| e == "c") {
            build.file(path);
        }
    }
    build.include(inc_dir);
    build.flag_if_supported("-Wall");
    build.flag_if_supported("-Wextra");
    build.compile("toci_firmware");

    println!("cargo:rerun-if-changed=../firmware/src");
    println!("cargo:rerun-if-changed=../firmware/include");
}
