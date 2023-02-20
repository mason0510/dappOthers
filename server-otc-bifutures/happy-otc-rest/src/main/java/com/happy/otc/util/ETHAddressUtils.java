package com.happy.otc.util;

import com.google.common.io.BaseEncoding;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import static com.google.common.primitives.Bytes.concat;

public class ETHAddressUtils {
    private static ECParameterSpec SPEC = ECNamedCurveTable.getParameterSpec("secp256k1");
    private final static int ADDRESS_LENGTH_IN_HEX = 40;
    private final String baseKey;
    private final DeterministicKey hdKey;
    private final BaseEncoding CODEC = BaseEncoding.base16().lowerCase();

    public ETHAddressUtils(String masterPub) {
        this.baseKey = masterPub;
        this.hdKey = HDKeyDerivation.deriveChildKey(ExtendedKey.parseDeterministicKey(masterPub),
                new ChildNumber(0, false)
        );
    }

    public DeterministicKey deriveChildPubKey(int index) {
        return HDKeyDerivation.deriveChildKey(hdKey, new ChildNumber(index, false));
    }

    public String deriveAddress(int index) {
        byte[] pubKey = compressedToUncompressed(deriveChildPubKey(index).getPubKey());
        return CODEC.encode(Keys.getAddress(pubKey));
    }

    private static byte[] compressedToUncompressed(byte[] compKey) {
        ECPoint point = SPEC.getCurve().decodePoint(compKey);
        byte[] x = point.getXCoord().getEncoded();
        byte[] y = point.getYCoord().getEncoded();
        return concat(x, y);
    }

    public static boolean isValidAddress(String input) {
        String cleanInput = Numeric.cleanHexPrefix(input);

        try {
            Numeric.toBigIntNoPrefix(cleanInput);
        } catch (NumberFormatException e) {
            return false;
        }

        return cleanInput.length() == ADDRESS_LENGTH_IN_HEX;
    }
}
