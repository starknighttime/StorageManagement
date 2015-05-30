package com.gamingbeast.storagemanagement;

import android.graphics.Bitmap;
import android.view.View;

public class Product implements Comparable<Product> {
	boolean isLocked = false;
	boolean showDetail = false;
	View detail;
	int PKEY;
	String name;
	Bitmap photo;
	float retailPrice;
	float wholesalePrice[];
	int wholesaleCount[];
	float lowestImPrice;
	float avImPrice;
	float avImPriceInStock;
	float avShipmentCostInStock;
	int quantity;
	int salesVolume;

	public Product(Builder build) {
		this.PKEY = build.PKEY;
		this.name = build.name;
		this.photo = build.photo;
		this.retailPrice = build.retailPrice;
		this.wholesalePrice = build.wholesalePrice;
		this.wholesaleCount = build.wholesaleCount;
		this.lowestImPrice = build.lowestImPrice;
		this.avImPrice = build.avImPrice;
		this.avImPriceInStock = build.avImPriceInStock;
		this.avShipmentCostInStock = build.avShipmentCostInStock;
		this.quantity = build.quantity;
		this.salesVolume = build.salesVolume;
	}

	@Override
	public int compareTo(Product another) {
		if (PKEY == another.PKEY) {
			return 0;
		}
		return 1;
	}

	public String toString() {
		return PKEY + " " + name;
	}

	public static class Builder {
		private int PKEY;
		private String name;
		private Bitmap photo;
		private float retailPrice;
		private float wholesalePrice[];
		private int wholesaleCount[];
		private float lowestImPrice;
		private float avImPrice;
		private float avImPriceInStock;
		private float avShipmentCostInStock;
		private int quantity;
		private int salesVolume;

		public Builder(int PKEY, String name) {
			this.PKEY = PKEY;
			this.name = name;
		}

		public Builder photo(Bitmap photo) {
			this.photo = photo;
			return this;
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
			this.wholesaleCount =wholesaleCount;
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

		public Product build() {
			return new Product(this);
		}
	}
}