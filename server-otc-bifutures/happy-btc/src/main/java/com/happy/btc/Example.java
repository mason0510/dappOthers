package com.happy.btc;

import org.bitcoinj.params.TestNet3Params;

public class Example {

    public static void main(String[] args) {
      int type = 2;
        AddressUtils utils = null;
      if (type == 1){
          String hdPublicKey = "tpubDCnbUuY1UXQKE63sdzYQxaoNb2qAC6B3tippjxBy8qoshcvKeAJTqyyQ8ruoU2A5oLS8nbhjigYwQwweuSRB9ytEhAoxZ88KBaaLUPGTgRx";
          // 正式环境去除 TestNet3Params.get() 参数，使用比特币主网
           utils = new AddressUtils(hdPublicKey,TestNet3Params.get());
      }else{
           String hdPublicKey = "xpub6CZXU7r9mAjJMs8FW5tami7drXA6UpqXsLW94GUveqX9KeS5nDQbgjavFrzSsnKMPeK4duxC5XSbouU6u8zZPBwKDQQSxUXsSNFXZgxGTwd";
          // 正式环境去除 TestNet3Params.get() 参数，使用比特币主网
           utils = new AddressUtils(hdPublicKey);
      }

        for (int i=0;i<20;i++){
            System.out.println("排序 "+i+" 地址 "+utils.deriveAddress(i));
        }
        System.out.println("排序 "+101+" 地址 "+utils.deriveAddress(101));
    }
}
