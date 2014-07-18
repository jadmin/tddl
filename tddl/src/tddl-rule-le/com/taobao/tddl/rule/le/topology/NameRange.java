package com.taobao.tddl.rule.le.topology;

import java.util.Arrays;
import java.util.List;

/**
 * ����һ���׺��Χ��
 * 
 * <pre>
 *   NamePattern = Name | ( Prefix SuffixExpr )
 *   
 *   SuffixExpr = "[" NameSuffix *( "," NameSuffix ) "]"
 *   
 *   NameSuffix = Pattern | NameRange
 *   
 *   NameRange = Min "-" Max
 * </pre>
 * 
 * @author changyuan.lh
 */
public final class NameRange extends NameSuffix {

    static final long[] pow10 = { 1, 10, 100, 1000, 10000, 100000, 1000000,
	    10000000, 100000000, 1000000000, 10000000000L, 100000000000L,
	    1000000000000L, 10000000000000L, 100000000000000L,
	    1000000000000000L, 10000000000000000L, 100000000000000000L,
	    1000000000000000000L };

    private int zeroPadding;

    private long min, max;

    static boolean numericCheck(String input) {
	final int len = input.length();
	if (len == 0) {
	    return false; // Empty input!
	}
	for (int i = 0; i < len; i++) {
	    if (!Character.isDigit(input.charAt(i)))
		return false;
	}
	return true;
    }

    static String paddingZero(final long number, final int zeroPadding) {
	final long pow = pow10[zeroPadding];
	if (number < pow) {
	    return String.valueOf(pow + number).substring(1);
	}
	// ��ֵ������ 0 ����, ���ԭʼֵ
	return String.valueOf(number);
    }

    public NameRange(String numeric) {
	this.zeroPadding = (numeric.charAt(0) == '0') ? numeric.length() : 0;
	this.min = Long.parseLong(numeric);
	this.max = min;
    }

    public NameRange(final long min, final long max, final int zeroPadding) {
	if (zeroPadding > 18) {
	    throw new IllegalArgumentException(
		    "ZeroPadding must less than 18, but given " + zeroPadding);
	}
	this.zeroPadding = zeroPadding;
	this.min = Math.min(min, max);
	this.max = Math.max(min, max);
    }

    public long getMin() {
	return min;
    }

    public long getMax() {
	return max;
    }

    public int getZeroPadding() {
	return zeroPadding;
    }

    private boolean merge(long min, long max, int zeroPadding) {
	if ((max < this.min - 1) || (min > this.max + 1)) {
	    // ��ֵ���ڷ�Χ��
	    return false;
	}
	if (zeroPadding > 0) {
	    if (this.zeroPadding == 0) {
		if (Long.toString(this.min).length() != zeroPadding
			|| Long.toString(this.max).length() != zeroPadding) {
		    // ��ֵ���Ȳ���ͬ
		    return false;
		}
		this.zeroPadding = zeroPadding;
	    } else if (this.zeroPadding != zeroPadding) {
		// �� 0 ���Ȳ���ͬ
		return false;
	    }
	} else if (zeroPadding == 0) {
	    if (this.zeroPadding != 0) {
		if (Long.toString(min).length() != this.zeroPadding
			|| Long.toString(max).length() != this.zeroPadding) {
		    // ��ֵ���Ȳ���ͬ
		    return false;
		}
	    }
	}
	if (min < this.min)
	    this.min = min;
	if (max > this.max)
	    this.max = max;
	return true;
    }

    public boolean put(long number, int zeroPadding) {
	return merge(number, number, zeroPadding);
    }

    public boolean merge(NameRange nameRange) {
	return merge(nameRange.min, nameRange.max, nameRange.zeroPadding);
    }

    public boolean contains(String name) {
	if (!numericCheck(name)) {
	    // ���ݲ�����ֵ
	    return false;
	}
	if (zeroPadding != 0 && name.length() != zeroPadding) {
	    // ��ֵ���ǹ̶�����
	    return false;
	}
	final long number = Long.parseLong(name);
	return (number >= min) && (number <= max);
    }

    protected List<String> iterate(StringBuilder buf, List<String> list) {
	final int len = buf.length();
	for (long number = min; number <= max; number++) {
	    if (zeroPadding != 0) {
		buf.append(paddingZero(number, zeroPadding));
	    } else {
		buf.append(number);
	    }
	    list.add(buf.toString());
	    buf.setLength(len);
	}
	return list;
    }

    /**
     * NameSuffix = Pattern | NameRange
     * 
     * NameRange = Min "-" Max
     */
    public static NameRange loadInput(String input) {
	final int len = input.length();
	final int minusIndex = input.indexOf('-', 0);
	if (minusIndex < 0) {
	    if (numericCheck(input)) {
		final long min = Long.parseLong(input);
		final int zeroPadding = (input.charAt(0) == '0') ? len : 0;
		return new NameRange(min, min, zeroPadding);
	    }
	    // Range ������ֵ, ����ʧ��
	    throw new IllegalArgumentException("Range not number: " + input);
	} else if (minusIndex < 1 || minusIndex + 1 >= len) {
	    // Range ǰ�����ݲ�ȫ, ����ʧ��
	    throw new IllegalArgumentException("Range not complete: " + input);
	}
	String min = input.substring(0, minusIndex);
	String max = input.substring(minusIndex + 1);
	if (!numericCheck(min) || !numericCheck(max)) {
	    // min, max ����һ��������ֵ, ����ʧ��
	    throw new IllegalArgumentException( // NL
		    "Range min/max not number: " + input);
	}
	if (min.charAt(0) == '0' || max.charAt(0) == '0') {
	    if (min.length() == max.length()) {
		return new NameRange(Long.parseLong(min), // NL
			Long.parseLong(max), min.length());
	    }
	}
	// Range ǰ�󳤶Ȳ����, ����Ҫ�� 0
	return new NameRange(Long.parseLong(min), Long.parseLong(max), 0);
    }

    protected StringBuilder buildString(StringBuilder buf) {
	if (max > min) {
	    if (zeroPadding != 0) {
		buf.append(paddingZero(min, zeroPadding));
		buf.append('-');
		buf.append(paddingZero(max, zeroPadding));
	    } else {
		buf.append(min);
		buf.append('-');
		buf.append(max);
	    }
	} else {
	    if (zeroPadding != 0) {
		buf.append(paddingZero(min, zeroPadding));
	    } else {
		buf.append(min);
	    }
	}
	return buf;
    }

    public static void main(String[] args) {
	NameRange nameRange = NameRange.loadInput("032-063");
	System.out.println("Range: " + nameRange);
	System.out.println("  contains 037: " + nameRange.contains("037"));
	System.out.println("  contains 121: " + nameRange.contains("121"));
	System.out.println("  contains 56: " + nameRange.contains("56"));
	System.out.println("list: "
		+ Arrays.toString(nameRange.list().toArray()));
	System.out.println("0112: " + NameRange.loadInput("0112"));
	System.out.println("12: " + NameRange.loadInput("12"));
	System.out.println();

	NameRange mergeRange = NameRange.loadInput("064-127");
	System.out.println("Range merge: " + mergeRange);
	System.out.println("  Return: " // NL
		+ nameRange.merge(mergeRange) + ", " + nameRange);
	System.out.println();

	NameRange simpleRange = NameRange.loadInput("128");
	System.out.println("Simple merge: " + simpleRange);
	System.out.println("  Return: " // NL
		+ nameRange.merge(simpleRange) + ", " + nameRange);
	System.out.println();
    }
}