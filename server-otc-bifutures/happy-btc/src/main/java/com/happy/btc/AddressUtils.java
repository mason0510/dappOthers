package com.happy.btc;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.WrongNetworkException;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.params.MainNetParams;

public class AddressUtils {
    private final DeterministicKey hdKey;
    private final NetworkParameters networkParameters;

    public AddressUtils(String masterPub) {
        this(masterPub, MainNetParams.get());
    }

    public AddressUtils(String masterPub, NetworkParameters params) {
        this.hdKey = HDKeyDerivation.deriveChildKey(ExtendedKey.parseDeterministicKey(masterPub),
                new ChildNumber(0, false)
        );

        this.networkParameters = params;
    }

    public String deriveAddress(int index) {
        DeterministicKey childKey = HDKeyDerivation.deriveChildKey(this.hdKey, new ChildNumber(index, false));
        return childKey.toAddress(this.networkParameters).toString();
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
