// Sample size

pub fn sample_size(confidence: f64, defect_rate: f64) -> u32 {
    ((1.0 - confidence).ln() / (1.0 - defect_rate).ln()).ceil() as u32
}

// Tests

#[cfg(test)]
mod tests {
    use super::*;

    #[test] fn default() { assert_eq!(sample_size(0.99, 0.01), 459); }
    #[test] fn strict()  { assert_eq!(sample_size(0.95, 0.001), 2995); }
}
