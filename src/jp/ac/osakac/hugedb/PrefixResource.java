package jp.ac.osakac.hugedb;

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