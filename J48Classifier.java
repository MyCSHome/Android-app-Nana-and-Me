package com.mybo.nanaAndme;

public class J48Classifier {

	/*  public static double classify(Object[] i)
    throws Exception {

    double p = Double.NaN;
    p = J48Classifier.N186d8c30(i);
    return p;
  }
  static double N186d8c30(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 0;
    } else if (((Double) i[2]).doubleValue() <= 2.0) {
      p = 0;
    } else if (((Double) i[2]).doubleValue() > 2.0) {
    p = J48Classifier.N105a91(i);
    } 
    return p;
  }
  static double N105a91(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 0.5646419610822551) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() > 0.5646419610822551) {
      p = 2;
    } 
    return p;
  }*/



	public static double classify(Object[] i)
			throws Exception {

		double p = Double.NaN;
		p = J48Classifier.N1c43fbe0(i);
		return p;
	}
	static double N1c43fbe0(Object []i) {
		double p = Double.NaN;
		if (i[4] == null) {
			p = 0;
		} else if (((Double) i[4]).doubleValue() <= 2.0) {
			p = 0;
		} else if (((Double) i[4]).doubleValue() > 2.0) {
			p = J48Classifier.N134ef8c1(i);
		} 
		return p;
	}
	static double N134ef8c1(Object []i) {
		double p = Double.NaN;
		if (i[1] == null) {
			p = 1;
		} else if (((Double) i[1]).doubleValue() <= 1.31014876096641) {
			p = 1;
		} else if (((Double) i[1]).doubleValue() > 1.31014876096641) {
			p = 2;
		} 
		return p;
	}
}