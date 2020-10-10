package ecc;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.SecureRandom;



class ECC {
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger THREE = BigInteger.valueOf(3);
    private int keyLength;

    private final BigInteger p, n, a;
    private BigIntegerPoint basePoint;

 
    ECC(String p, String n, String a, BigIntegerPoint basePoint) {
        this.p = new BigInteger(p);
        this.n = new BigInteger(n);
        this.a = new BigInteger(a).mod(this.p);
        this.basePoint = basePoint;
        this.keyLength = this.n.bitLength();
    }

 
    private void pointDouble(BigIntegerPoint point) {
        BigInteger lambda =  THREE.multiply(point.x.pow(2))
                                  .add(a)
                                  .multiply(TWO.multiply(point.y).modInverse(p))
                                  .mod(p);
        
        pointCalculate(point, point, lambda);
    }

    
    private void pointAddition(BigIntegerPoint point, BigIntegerPoint basePoint) {
        BigInteger deltaY = point.y.subtract(basePoint.y);
        BigInteger deltaX = point.x.subtract(basePoint.x);
        BigInteger lambda = deltaY.multiply(deltaX.modInverse(p))
                                   .mod(p);
        
        pointCalculate(point, basePoint, lambda);
    }

    private void pointCalculate(BigIntegerPoint point, BigIntegerPoint point2, BigInteger lambda) {
        BigInteger xR = lambda.pow(2)
                              .subtract(point.x)
                              .subtract(point2.x)
                               .mod(p);

        BigInteger yR = lambda.multiply(point.x.subtract(xR))
                              .subtract(point.y)
                              .mod(p);
        point.x = xR;
        point.y = yR;
    }



    public BigIntegerPoint pointMultipication(BigIntegerPoint startPoint, BigInteger key, boolean inverse) {
        BigIntegerPoint point = startPoint.copy();
        if (inverse) key = key.modInverse(n);
        String binaryKey = key.toString(2);
        binaryKey = binaryKey.substring(1,binaryKey.length());

        for (char digit : binaryKey.toCharArray()) {
            pointDouble(point);
            if (digit == '1') pointAddition(point,startPoint);
        }

        return point;
    }

    public BigIntegerPoint pointMultipication(BigInteger key, boolean inverse) {
        return pointMultipication(basePoint, key, inverse);
    }

    public void checkKey(BigInteger key) throws InvalidKeyException {
        if (key.compareTo(n.subtract(TWO)) >= 0) {
            throw new InvalidKeyException("key must be less than " + n);
        }
    }


    BigInteger generateKey() {
        SecureRandom random = new SecureRandom();
        BigInteger key;
        do {
            key = new BigInteger(keyLength, random);
        } while(key.compareTo(n.subtract(TWO)) >= 0);
        return key;
    }
    
}
