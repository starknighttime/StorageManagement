package com.gamingbeast.storagemanagement;

public class Product {
	boolean isLocked = false;
	boolean showDetail = false;
	boolean isSelected = false;
	int amount = 0;
	
	int PKEY;
	String name;
	float retailPrice;
	float wholesalePrice[];
	int wholesaleCount[];
	float lowestImPrice;
	float avImPrice;
	float avImPriceInStock;
	float avShipmentCostInStock;
	String lowestImTime;
	String lowestImSource;
	int quantity;
	int salesVolume;
	int quantityIm;
	int quantityEx;
	int quantityC;

	public Product(Builder build) {
		this.PKEY = build.PKEY;
		this.name = build.name;
		this.retailPrice = build.retailPrice;
		this.wholesalePrice = build.wholesalePrice;
		this.wholesaleCount = build.wholesaleCount;
		this.lowestImPrice = build.lowestImPrice;
		this.avImPrice = build.avImPrice;
		this.avImPriceInStock = build.avImPriceInStock;
		this.avShipmentCostInStock = build.avShipmentCostInStock;
		this.quantity = build.quantity;
		this.salesVolume = build.salesVolume;
		this.quantityIm = build.quantityIm;
		this.quantityEx = build.quantityEx;
		this.quantityC = build.quantityC;
		this.lowestImTime = build.lowestImTime;
		this.lowestImSource = build.lowestImSource;
	}

	public Product(int PKEY) {
		this.PKEY = PKEY;
	}

	@Override
	public boolean equals(Object another) {
		if (another instanceof Product) {
			if (PKEY == ((Product) another).PKEY) {
				return true;
			} else {
				return false;
			}
		}
		return super.equals(another);
	}

	public String toString() {
		return PKEY + " " + name;
	}

	public static class Builder {
		private int PKEY;
		private String name;
		private float retailPrice;
		private float wholesalePrice[];
		private int wholesaleCount[];
		private float lowestImPrice;
		private float avImPrice;
		private float avImPriceInStock;
		private float avShipmentCostInStock;
		private int quantity;
		private int salesVolume;
		private int quantityIm;
		private int quantityEx;
		private int quantityC;
		private String lowestImTime;
		private String lowestImSource;

		public Builder(int PKEY, String name) {
			this.PKEY = PKEY;
			this.name = name;
		}

		public Builder retailPrice(float retailPrice) {
			this.retailPrice = retailPrice;
			return this;
		}

		public Builder wholesalePrice(float[] wholesalePrice) {
			this.wholesalePrice = wholesalePrice;
			return this;
		}

		public Builder wholesaleCount(int[] wholesaleCount) {
			this.wholesaleCount = wholesaleCount;
			return this;
		}

		public Builder lowestImPrice(float lowestImPrice) {
			this.lowestImPrice = lowestImPrice;
			return this;
		}

		public Builder avImPrice(float avImPrice) {
			this.avImPrice = avImPrice;
			return this;
		}

		public Builder avImPriceInStock(float avImPriceInStock) {
			this.avImPriceInStock = avImPriceInStock;
			return this;
		}

		public Builder avShipmentCostInStock(float avShipmentCostInStock) {
			this.avShipmentCostInStock = avShipmentCostInStock;
			return this;
		}

		public Builder quantity(int quantity) {
			this.quantity = quantity;
			return this;
		}

		public Builder salesVolume(int salesVolume) {
			this.salesVolume = salesVolume;
			return this;
		}
		
		public Builder quantityIm(int quantityIm) {
			this.quantityIm = quantityIm;
			return this;
		}
		public Builder quantityEx(int quantityEx) {
			this.quantityEx = quantityEx;
			return this;
		}
		public Builder quantityC(int quantityC) {
			this.quantityC = quantityC;
			return this;
		}
		public Builder lowestImTime(String lowestImTime) {
			this.lowestImTime = lowestImTime;
			return this;
		}
		public Builder lowestImSource(String lowestImSource) {
			this.lowestImSource = lowestImSource;
			return this;
		}

		public Product build() {
			return new Product(this);
		}
	}
}