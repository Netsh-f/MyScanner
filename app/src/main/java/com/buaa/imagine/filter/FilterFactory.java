/**
 * Project Name: Imagine
 *    File Name: FilterFactory.java
 *   Programmer: Tony Skywalker
 *   Start Date: November 18, 2022
 *  Last Update:
 *     Overview:
 */

package com.buaa.imagine.filter;

public class FilterFactory {
	/**
	 * Get specified filter by FilterType.
	 *
	 * @param type filter type
	 * @return return specific filter
	 */
	public static Filter getFilter(FilterType type) {
		switch (type) {
			case DOCUMENT:
				return new DocumentFilter();
			case ORIGINAL:
				return new OriginalFilter();
			case ROTATE:
				return new RotateFilter();
			default:
				return null;
		}
	}
}
