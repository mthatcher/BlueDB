package io.bluedb.api.keys;

public class IntegerKey implements BlueKey {
	private static final long serialVersionUID = 1L;

	private int id;

	public IntegerKey(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		IntegerKey other = (IntegerKey) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "IntegerKey [key=" + id + "]";
	}
}