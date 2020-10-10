package ecc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.ArrayList;

public class Main {

    public static String getPropertiesPath(String propertiesFile) {
        String packageLocation = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        Path packagePath = Paths.get(packageLocation);
        return Paths.get(packagePath.getParent().toString() + "/" + propertiesFile).toString();
        
    }

    public static final String PROPERTIES = getPropertiesPath("properties.ecc");

    public static String[] getValuesFromFile() {
        System.out.println();

        BufferedReader propertiesReader = null;
        try {
            propertiesReader = new BufferedReader(new FileReader(PROPERTIES));
            String line;
            ArrayList<String> values = new ArrayList<>();
            while ((line = propertiesReader.readLine()) != null) {
                if (!line.startsWith("#") && line.length() > 0)
                    values.add(line);
            }
            return values.toArray(new String[values.size()]);

        } catch (FileNotFoundException e) {
            System.out.println(PROPERTIES + " is missing");
        } catch (IOException e) {
            System.out.println("Unable to read from file " + PROPERTIES);
        } finally {
            if (propertiesReader != null) {
                try {
                    propertiesReader.close();
                } catch (IOException e) {
                    System.out.println("An unexpected problem occured while trying to read " + PROPERTIES);
                }
            }
        }
        return null;
    }

    public static String getHelpContent() {
        StringBuilder helpBuilder = new StringBuilder();
        helpBuilder.append("Usage: ecc --x x_value -y y_value --inverse [--key key_value | --random-key]\n");
        helpBuilder.append("--x [x_value] :  Sets the starting point x value.\n");
        helpBuilder.append("--y [y_value] :  Sets the starting point y value.\n");
        helpBuilder.append("Must contain both x and y or neither.\n");
        helpBuilder.append("--key [x_value] :  Sets the key input.\n");
        helpBuilder.append("--random-key:  Sets the input key with a random key.\n");
        helpBuilder.append("--show-key:  Prints the key.\n");
        helpBuilder.append("--help:  Prints this message.\n");
        helpBuilder.append("--inverse:  Perform point multipication inverse.\n");


        return helpBuilder.toString();

    }

    public static Tuple<BigIntegerPoint, BigInteger> eccMultiply(BigInteger x, BigInteger y, BigInteger key,
            boolean randomKey, boolean inverse) throws InvalidKeyException {

        if ( (x == null && y != null) || (x != null && y == null)) {
            throw new IllegalArgumentException("Must contain both x and y or neither.");
        }
        if (key == null && !randomKey) {
            throw new IllegalArgumentException("key is missing.");
        }
        String[] properties = getValuesFromFile();

        String p = properties[0];
        String n = properties[1];
   
        BigIntegerPoint startPoint = new BigIntegerPoint(properties[2],properties[3]);
        String a = properties[4];
        ECC ecc = new ECC(p,n,a,startPoint);
        if (randomKey) key = ecc.generateKey();
        else ecc.checkKey(key);

        if (x == null && y == null) return new Tuple<BigIntegerPoint,BigInteger>(ecc.pointMultipication(key,inverse),key);
        else return new Tuple<BigIntegerPoint,BigInteger>(ecc.pointMultipication(new BigIntegerPoint(x, y), key, inverse),key);

    }


    public static void main(String[] args) {


            BigInteger x = null, y = null, key = null;
            boolean printKey = false;
            boolean randomKey = false;
            boolean inverse = false;
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--x": {
                        x = i+1 < args.length ? new BigInteger(args[i+1]) : null;
                        break;
                    }
                    case "--y": {
                        y = i+1 < args.length ? new BigInteger(args[i+1]) : null;
                        break;
                    }
                    case "--key": {
                        key = i+1 < args.length ? new BigInteger(args[i+1]) : null;
                        break;
                    }
                    case "--random-key": {
                        randomKey = true;
                        break;
                    }
                    case "--show-key": {
                        printKey = true;
                        break;
                    }
                    case "--help": {
                        System.out.println(getHelpContent());
                        return;
                    }
                    case "--inverse": {
                        inverse = true;
                        break;
                    }

                }  
            }
            try {
                Tuple<BigIntegerPoint,BigInteger> resultAndKey = eccMultiply(x,y,key,randomKey,inverse);
                BigIntegerPoint result = resultAndKey.getFirst();

                if (printKey) {
                    System.out.println("key: " + resultAndKey.getSecond());
                }
        
                System.out.println("x; " + result.x);
                System.out.println("y; " + result.y);

            } catch (IllegalArgumentException|InvalidKeyException e) {
                System.out.println(e.getMessage());
            } 
        }


}
