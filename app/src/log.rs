// Logger

pub fn init() {
    android_logger::init_once(
        android_logger::Config::default()
            .with_tag("toci")
            .with_max_level(log::LevelFilter::Info),
    );
}
