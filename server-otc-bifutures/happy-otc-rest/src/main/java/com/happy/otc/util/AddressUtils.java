package com.happy.otc.util;

import com.google.common.io.BaseEncoding;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.params.MainNetParams;


public class AddressUtils {
    private final String baseKey;
    private final DeterministicKey hdKey;
    private final NetworkParameters networkParameters;
    private final BaseEncoding CODEC = BaseEncoding.base16().lowerCase();
    private static AddressUtils instance;
    private final static String LOCK = "LOCK";

    private static void init(String masterPub, NetworkParameters params) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new AddressUtils(masterPub,params);
                }
            }
        }
    }

    public static AddressUtils getInstance(String masterPub, NetworkParameters params){
        init(masterPub, params);
        return instance;
    }

    public AddressUtils(String masterPub) {
        this(masterPub, MainNetParams.get());
    }

    public AddressUtils(String masterPub, NetworkParameters params) {
        this.baseKey = masterPub;
        this.hdKey = HDKeyDerivation.deriveChildKey(ExtendedKey.parseDeterministicKey(masterPub),
                new ChildNumber(0, false)
        );

        this.networkParameters = params;
    }

    public DeterministicKey deriveChildPubKey(int index) {
        return HDKeyDerivation.deriveChildKey(this.hdKey, new ChildNumber(index, false));
    }

    public byte[] getChildXPubKey(int index) {
        String pubKey = CODEC.encode(Base58.decode(baseKey));
        String suffix = CODEC.encode(new byte[] {
                (byte) index, (byte) (index >> 8)
        });
        return CODEC.decode("ff" + pubKey.substring(0, pubKey.length() - 8) + "0000" + suffix);
    }

    public String deriveAddress(int index) {

        return deriveChildPubKey(index).toAddress(this.networkParameters).toString();
    }

    public static Boolean checkAddress(String address, NetworkParameters params) {
        Boolean result = true;
        try {
            Address.fromBase58(params, address);
        } catch (WrongNetworkException e) {
            result = false;
        } catch (AddressFormatException e) {
            result = false;
        }
        return result;
    }
}
