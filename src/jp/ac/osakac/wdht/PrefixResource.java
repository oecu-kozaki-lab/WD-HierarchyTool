package jp.ac.osakac.wdht;

class PrefixResource {
	public Integer index;
	public String value;
	public PrefixResource(Integer index, String value_) {
		if (index != null) {
			this.index = index.intValue();
		}
		this.value = value_;
	}
}