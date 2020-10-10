package ecc;

import java.math.BigInteger;

public class BigIntegerPoint {

    public BigInteger x,y;
    
    public BigIntegerPoint(final String x, final String y) {
        this.x = new BigInteger(x);
        this.y = new BigInteger(y);
    }

    public BigIntegerPoint(final BigInteger x, final BigInteger y) {
        this.x = x;
        this.y = y;
    }

    public BigIntegerPoint copy() {

        return new BigIntegerPoint(new BigInteger(this.x.toString()),
                                    new BigInteger(this.y.toString()));
    }
}