package domain;

public enum AudioQuality {
    STANDARD(128),
    HIGH(256),
    PREMIUM(320);

    private final int bitRateKbps;

    AudioQuality(int bitRateKbps) {
        this.bitRateKbps = bitRateKbps;
    }

    public int getBitRateKbps() {
        return bitRateKbps;
    }
}
