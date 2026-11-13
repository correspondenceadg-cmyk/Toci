# Verification

## Sample size

`sample_size(0.99, 0.01) -> 459`

`sample_size(0.95, 0.001) -> 2995`

## Per-LBA classification

| Class | Condition | Verdict |
|---|---|---|
| PASS_ZERO | All zeroes | Pass |
| PASS_UNWRITTEN | Zero or EIO consistent with discarded block | Pass |
| FAIL_ORIGINAL | Matches pre-wipe hash | Fail |
| FAIL_UNKNOWN | Neither zero, nor unwritten, nor original | Fail |

## What "pass" means

No sampled LBA returned its original data at the selected confidence. It does not mean every physical cell on the die was touched. No host-side operation can prove that.

## BLKDISCARDZEROES trap

If the device reports `discard-zeroes = 0`, a zero-read is `PASS_UNWRITTEN`, not `PASS_ZERO`. Treating it as `PASS_ZERO` would be a false pass on a controller that silently returns zeroes for unreadable blocks.
