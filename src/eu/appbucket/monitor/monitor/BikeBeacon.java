package eu.appbucket.monitor.monitor;

public class BikeBeacon {

	private Integer assetId;
	private String uudi;
	private int major;
	private int minor;
	
	public BikeBeacon() {
	}
			
	public BikeBeacon(Integer assetId, String uudi, int major, int minor) {
		this.assetId = assetId;
		this.uudi = uudi;
		this.major = major;
		this.minor = minor;
	}
	
	public BikeBeacon(String uudi, int major, int minor) {
		this.uudi = uudi;
		this.major = major;
		this.minor = minor;
	}
	
	public Integer getAssetId() {
		return assetId;
	}
	public void setAssetId(Integer assetId) {
		this.assetId = assetId;
	}
	public String getUudi() {
		return uudi;
	}
	public void setUudi(String uudi) {
		this.uudi = uudi;
	}
	public int getMajor() {
		return major;
	}
	public void setMajor(int major) {
		this.major = major;
	}
	public int getMinor() {
		return minor;
	}
	public void setMinor(int minor) {
		this.minor = minor;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + major;
		result = prime * result + minor;
		result = prime * result + ((uudi == null) ? 0 : uudi.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BikeBeacon other = (BikeBeacon) obj;
		if (major != other.major)
			return false;
		if (minor != other.minor)
			return false;
		if (uudi == null) {
			if (other.uudi != null)
				return false;
		} else if (!uudi.equals(other.uudi))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BeaconRecord [uudi=" + uudi + ", major=" + major + ", minor="
				+ minor + "]";
	}
}
