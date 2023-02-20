package com.happy.otc.controller;

import com.alibaba.fastjson.JSONArray;
import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.bitan.common.login.LoginDataHelper;
import com.bitan.common.vo.Result;
import com.happy.otc.annotation.CapitalCipherCheck;
import com.happy.otc.proto.CommodityExtendVO;
import com.happy.otc.service.ITransactionPairService;
import com.happy.otc.service.IUserService;
import com.happy.otc.util.ProtoUtils;
import com.happy.otc.vo.CommodityInfoVO;
import com.happy.otc.entity.Commodity;
import com.happy.otc.entity.UserAccount;
import com.happy.otc.enums.CommodityTypeEnum;
import com.happy.otc.enums.KindEnum;
import com.happy.otc.enums.PayMethodEnums;
import com.happy.otc.enums.RelevantKindEnum;
import com.happy.otc.proto.Offer;
import com.happy.otc.proto.Payaccount;
import com.happy.otc.service.ICommodityService;
import com.happy.otc.service.IUserAccountService;
import com.github.pagehelper.PageInfo;
import com.google.protobuf.InvalidProtocolBufferException;
import com.happy.otc.vo.ExchangeInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping(value = "/otc-rest")
@Api(value = "/otc-rest", description = "货币商品Api")
public class CommodityController {

    @Autowired
    ICommodityService commodityService;
    @Autowired
    ITransactionPairService transactionPairService;
    @Autowired
    private IUserAccountService userAccountService;
    @Autowired
    private IUserService userService;

    @ApiOperation(value = "货币商品列表")
    @RequestMapping(value = "/commodity-list", method = RequestMethod.GET)
    public Offer.ListOfferDTO CommodityInfoList(@ApiParam("商品类别 0：在买 1：在卖") @RequestParam(value = "commodityType" ,defaultValue = "0") Integer commodityType,
                                                @ApiParam("币种简称 BTC ETH") @RequestParam(value = "from") String from,
                                                @ApiParam("法币简称 人民币：CNY 美元：USD") @RequestParam(value = "to") String to,
                                                @ApiParam("可购买金额") @RequestParam(value = "minMoney",required = false,defaultValue = "-1") Double minMoney,
                                                @ApiParam("支付类别 0：全部 1：Alipay 2：WECHAT_PAY 3：bank_pay") @RequestParam(value = "payType",defaultValue ="0") Integer payType,
                                                @RequestParam(value = "pageIndex") Integer pageIndex,
                                                @RequestParam(value = "pageSize") Integer pageSize){

        return queryCommodityData(commodityType,from,to,minMoney,payType, pageIndex, pageSize);
    }

    @ApiOperation(value = "货币商品列表")
    @RequestMapping(value = "/commodity-list-extend", method = RequestMethod.GET)
    public Offer.ListOfferDTO CommodityInfoList2(@ApiParam("商品类别 0：在买 1：在卖") @RequestParam(value = "commodityType" ,defaultValue = "0") Integer commodityType,
                                                @ApiParam("币种简称 BTC ETH") @RequestParam(value = "from") String from,
                                                @ApiParam("法币简称 人民币：CNY 美元：USD") @RequestParam(value = "to") String to,
                                                @ApiParam("可购买金额") @RequestParam(value = "minMoney",required = false,defaultValue = "-1") Double minMoney,
                                                @ApiParam("支付类别 0：全部 1：Alipay 2：WECHAT_PAY 3：bank_pay") @RequestParam(value = "payType",defaultValue ="0") Integer payType,
                                                @RequestParam(value = "pageIndex") Integer pageIndex,
                                                @RequestParam(value = "pageSize") Integer pageSize){
       return queryCommodityData(commodityType,from,to,minMoney,payType, pageIndex, pageSize);
    }

    public Offer.ListOfferDTO queryCommodityData(Integer commodityType,
                                                     String from,
                                                     String to,
                                                     Double minMoney,
                                                     Integer payType,
                                                     Integer pageIndex,
                                                     Integer pageSize){
        BizException.isNull(pageIndex, "pageIndex");
        BizException.isNull(pageSize, "pageSize");

        KindEnum kindEnum = KindEnum.getInstance(from);
        if (Objects.isNull(kindEnum)){
            kindEnum = KindEnum.BTC;
        }
        RelevantKindEnum relevantKindEnum = RelevantKindEnum.getInstance(to);
        if (Objects.isNull(relevantKindEnum)){
            relevantKindEnum = RelevantKindEnum.CNY;
        }


        CommodityTypeEnum commodityTypeEnum = CommodityTypeEnum.getInstance(commodityType);
        //因为用户是在购买商品，所以要把在卖商品显示在前台
        if (CommodityTypeEnum.BUY == commodityTypeEnum){
            commodityType = CommodityTypeEnum.SELL.getValue();
        }else if ( CommodityTypeEnum.SELL == commodityTypeEnum){
            //因为用户是在售卖商品，所以要把收购商品显示在前台
            commodityType = CommodityTypeEnum.BUY.getValue();
        }else {
            BizException.fail(ApiResponseCode.PARA_ERR," commodityType");
        }

        Map<String, Object> params = new HashMap<>();
        //支付类别处理
        PayMethodEnums payMethodEnums = PayMethodEnums.getInstance(payType);
        if (!Objects.isNull(payMethodEnums)){
            params.put("payType",payMethodEnums.getValue()+"");
        }

        params.put("commodityType",commodityType);
        params.put("relevantKind",relevantKindEnum.getValue());
        params.put("kind",kindEnum.getValue());
        //不对未登陆进行校验
        try {
            Long userId = LoginDataHelper.getUserId();

            params.put("selfUserId",userId);
        }catch (Exception e){
        }

        //可购买金额
        if (minMoney.compareTo( Double.valueOf( "0" )) > 0 ){
            params.put("minMoney",minMoney);
        }

        PageInfo<CommodityInfoVO> pageInfo = commodityService.getCommodityListByParam(
                params,
                pageIndex,
                pageSize);


        //请求数据的封装
        List<Offer.OfferDTO> listDTO = new ArrayList<>();
        for (CommodityInfoVO commodityInfoVO:pageInfo.getList()) {
            Offer.OfferDTO.Builder commodityVO = Offer.OfferDTO.newBuilder();
            commodityVO.setCommodityId(commodityInfoVO.getCommodityId());
            commodityVO.setCurrencyId(commodityInfoVO.getCurrencyId());
            commodityVO.setKind(commodityInfoVO.getKind());
            commodityVO.setRelevantKind(commodityInfoVO.getRelevantKind());
            commodityVO.setRelevantKind(commodityInfoVO.getRelevantKind());
            commodityVO.setMinimumAmount(commodityInfoVO.getMinimumAmount().doubleValue());
            commodityVO.setMaximumAmount(commodityInfoVO.getMaximumAmount().doubleValue());
//            if (commodityInfoVO.getTradeMethod() != null && commodityInfoVO.getTradeMethod() == CommodityExtendVO.TradeMethod.TRADE_MARKET.getNumber()){
//                BigDecimal b = new BigDecimal(price*0.01*commodityInfoVO.getCurrencyPrice().doubleValue());
//                commodityVO.setCurrencyPrice(b.setScale(2,BigDecimal.ROUND_DOWN).doubleValue());
//            }else{
            commodityVO.setCurrencyPrice(commodityInfoVO.getCurrencyPrice().doubleValue());
//            }
            commodityVO.setCurrencyQuantity(commodityInfoVO.getCurrencyQuantity().doubleValue());
            commodityVO.setCommodityId(commodityInfoVO.getCommodityId());
            if (!StringUtils.isEmpty(commodityInfoVO.getNickName())){
                commodityVO.setNickName(commodityInfoVO.getNickName());
            }
            commodityVO.setTransactionCount(commodityInfoVO.getTotalTradeCount());
            commodityVO.setTotalTradeCount(commodityInfoVO.getTotalTradeCount());
            commodityVO.setCloseRate(commodityInfoVO.getCloseRate());
            commodityVO.setUserId(commodityInfoVO.getUserId());
            commodityVO.setVersionNumber(commodityInfoVO.getVersionNumber());
            try {
                commodityVO.setTradeCurb(CommodityExtendVO.TradeCurbDTO.parseFrom(commodityInfoVO.getTradeCurb().getBytes()));
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            JSONArray payMethodArray = JSONArray.parseArray(commodityInfoVO.getPayMethodTypeList());
            List<Payaccount.PaymentType> paymentTypeList = new ArrayList<>();
            if (payMethodArray != null){
                for (int i = 0;i < payMethodArray.size(); i++){
                    Integer type  = Integer.valueOf(payMethodArray.get(i).toString());
                    paymentTypeList.add(Payaccount.PaymentType.forNumber(type));
                }
                commodityVO.addAllPaymentMethodList(paymentTypeList);
            }

            listDTO.add(commodityVO.build());
        }
        Offer.ListOfferDTO listOfferDTO = Offer.ListOfferDTO.getDefaultInstance();
        return ProtoUtils.createPageResultSuccess(listDTO, pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), listOfferDTO);
    }
    @ApiOperation(value = "添加商品")
    @RequestMapping(value = "/add-commodity", method = RequestMethod.POST)
    @CapitalCipherCheck
    public Result<Long> addCommodity(@RequestBody CommodityExtendVO.CommodityVOParam  commodityVO)throws BizException {
        Long userId = LoginDataHelper.getUserId();
        BizException.isNull(commodityVO.getCommodityType(), "商品类别");
        BizException.isNull(commodityVO.getCapitalCipher(), "资金密码");
        KindEnum kindEnum = KindEnum.getInstance(commodityVO.getKind());
        if (Objects.isNull(kindEnum)){
            BizException.fail(ApiResponseCode.PARA_ERR,null);
        }
        RelevantKindEnum relevantKindEnum = RelevantKindEnum.getInstance(commodityVO.getRelevantKind());
        if (Objects.isNull(relevantKindEnum)){
            BizException.fail(ApiResponseCode.PARA_ERR,null);
        }
        Long result = commodityService.addCommodity(commodityVO, userId);
        return  Result.createSuccess(result);
    }

    @ApiOperation(value = "下架商品")
    @RequestMapping(value = "/del-commodity", method = RequestMethod.POST)
    public Result<Boolean> delCommodity(@ApiParam("商品id")@RequestParam(value = "commodityId") Long commodityId){
        Long userId = LoginDataHelper.getUserId();
        BizException.isNull(commodityId, "commodityId");

        Boolean result = commodityService.delCommodity(commodityId);
        return  Result.createSuccess(result);
    }

    @ApiOperation(value = "修改商品属性")
    @RequestMapping(value = "/update-commodity", method = RequestMethod.POST)
    @CapitalCipherCheck
    public Result<Boolean> updateCommodity(@RequestBody CommodityExtendVO.CommodityVOParam commodityVO){
        Long userId = LoginDataHelper.getUserId();
        BizException.isNull(commodityVO.getCommodityId(), "commodityId");

        Boolean result = commodityService.updateCommodity(commodityVO);
        return  Result.createSuccess(result);
    }
    @ApiOperation(value = "购买商品")
    @RequestMapping(value = "/buy-commodity", method = RequestMethod.POST)
    @CapitalCipherCheck
    public Result<Long> buyCommodity(@ApiParam(" 购入金额(用户消费的金额)") @RequestParam(value = "transactionAmount") BigDecimal transactionAmount,
                                        @ApiParam("购入交易量(用户购买的货币数量)") @RequestParam(value = "transactionVolume") BigDecimal transactionVolume,
                                        @ApiParam("商品id") @RequestParam(value = "commodityId") Long commodityId,
                                        @ApiParam("资金密码(出售商品时需要)") @RequestParam(value = "capitalCipher",required = false) String capitalCipher){

        BizException.isNull(transactionAmount, "transactionAmount");
        BizException.isNull(transactionVolume, "transactionVolume");

        Long userId = LoginDataHelper.getUserId();

        Long capitalDetailId = commodityService.buyCommodity(commodityId,transactionVolume,transactionAmount,userId,capitalCipher);
        return  Result.createSuccess("创建交易订单成功", capitalDetailId);
    }

    @ApiOperation(value = "查看用户货币商品列表")
    @RequestMapping(value = "/user-commodity-list", method = RequestMethod.GET)
    public CommodityExtendVO.UserCommodityDTOListResult commodityInfoList(
            @RequestParam(value = "kind",required = false) String kind,
            @RequestParam(value = "pageIndex") Integer pageIndex,
            @RequestParam(value = "pageSize") Integer pageSize) {

        Long userId = LoginDataHelper.getUserId();

        PageInfo<CommodityInfoVO> pageInfo = commodityService.getUserCommodityList(
                userId,
                kind,
                pageIndex,
                pageSize);
        ExchangeInfoVO exchangeInfoBTC = transactionPairService.getExchangeInfoVO("BTC");
        ExchangeInfoVO exchangeInfoUSDT = transactionPairService.getExchangeInfoVO("USDT");

        //请求数据的封装
        List<CommodityExtendVO.UserCommodityDTO> listDTO = new ArrayList<>();
        for (CommodityInfoVO commodityInfoVO : pageInfo.getList()) {
            CommodityExtendVO.UserCommodityDTO.Builder userCommodityDTO = CommodityExtendVO.UserCommodityDTO.newBuilder();
            userCommodityDTO.setCommodityId(commodityInfoVO.getCommodityId());
            userCommodityDTO.setCreateTime(commodityInfoVO.getCreateTime().getTime());
            userCommodityDTO.setKind(commodityInfoVO.getKind());
            userCommodityDTO.setRelevantKind(commodityInfoVO.getRelevantKind());
            userCommodityDTO.setCurrencyId(commodityInfoVO.getCurrencyId());
            userCommodityDTO.setTradeMethodValue( commodityInfoVO.getTradeMethod());
            userCommodityDTO.setMinimumAmount(commodityInfoVO.getMinimumAmount().doubleValue());
            userCommodityDTO.setMaximumAmount(commodityInfoVO.getMaximumAmount().doubleValue());

            if (commodityInfoVO.getTradeMethod().equals(CommodityExtendVO.TradeMethod.TRADE_MARKET.getNumber())){
                userCommodityDTO.setPricePercentage(commodityInfoVO.getCurrencyPrice().doubleValue());
                if (commodityInfoVO.getKind().equals(KindEnum.BTC.getValue())) {
                    //BTC
                    if (commodityInfoVO.getRelevantKind().equals(RelevantKindEnum.CNY.getValue())) {
                        userCommodityDTO.setTradePrice(BigDecimal.valueOf(exchangeInfoBTC.getPriceCNY() * 0.01 * (100 + commodityInfoVO.getCurrencyPrice().doubleValue())).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    } else if (commodityInfoVO.getRelevantKind().equals(RelevantKindEnum.USD.getValue())) {
                        userCommodityDTO.setTradePrice(BigDecimal.valueOf(exchangeInfoBTC.getPriceUSD() * 0.01 * (100 + commodityInfoVO.getCurrencyPrice().doubleValue())).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    } else if (commodityInfoVO.getRelevantKind().equals(RelevantKindEnum.CAD.getValue())) {
                        userCommodityDTO.setTradePrice(BigDecimal.valueOf(exchangeInfoBTC.getPriceCAD() * 0.01 * (100 + commodityInfoVO.getCurrencyPrice().doubleValue())).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    }
                } else {
                    //USDT
                    if (commodityInfoVO.getRelevantKind().equals(RelevantKindEnum.CNY.getValue())) {
                        userCommodityDTO.setTradePrice(BigDecimal.valueOf(exchangeInfoUSDT.getPriceCNY() * 0.01 * (100 + commodityInfoVO.getCurrencyPrice().doubleValue())).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    } else if (commodityInfoVO.getRelevantKind().equals(RelevantKindEnum.USD.getValue())) {
                        userCommodityDTO.setTradePrice(BigDecimal.valueOf(exchangeInfoUSDT.getPriceUSD() * 0.01 * (100 + commodityInfoVO.getCurrencyPrice().doubleValue())).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    } else if (commodityInfoVO.getRelevantKind().equals(RelevantKindEnum.CAD.getValue())) {
                        userCommodityDTO.setTradePrice(BigDecimal.valueOf(exchangeInfoUSDT.getPriceCAD() * 0.01 * (100 + commodityInfoVO.getCurrencyPrice().doubleValue())).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                    }
                }
            }else {
                userCommodityDTO.setTradePrice(commodityInfoVO.getCurrencyPrice().doubleValue());
            }
            userCommodityDTO.setCommodityTypeValue(commodityInfoVO.getCommodityType());
            if (commodityInfoVO.getCurrencyQuantity().compareTo(BigDecimal.ZERO) == 0){
                userCommodityDTO.setCurrencyAmount("0");
            }else {
                userCommodityDTO.setCurrencyAmount(commodityInfoVO.getCurrencyQuantity().setScale(6,BigDecimal.ROUND_FLOOR).toString());
            }

            listDTO.add(userCommodityDTO.build());
        }
        CommodityExtendVO.UserCommodityDTOListResult result = CommodityExtendVO.UserCommodityDTOListResult.getDefaultInstance();
        return ProtoUtils.createPageResultSuccess(listDTO, pageInfo.getPageNum(), pageInfo.getPageSize(),
                pageInfo.getTotal(), result);
    }

    @ApiOperation(value = "查看用户商品详情")
    @RequestMapping(value = "/user-commodity-detail", method = RequestMethod.GET)
    public CommodityExtendVO.CommodityVOResult commodityDetail(@ApiParam("商品id")@RequestParam(value = "commodityId") Long commodityId){

        Long userId = LoginDataHelper.getUserId();

        Commodity commodity = commodityService.getCommodityById(commodityId);
        if (commodity == null){
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "数据不存在");
        }

        //请求数据的封装
        CommodityExtendVO.CommodityVO.Builder commodityVO = CommodityExtendVO.CommodityVO.newBuilder();
        commodityVO.setCommodityId(commodity.getCommodityId());
        commodityVO.setKind(commodity.getKind());
        commodityVO.setRelevantKind(commodity.getRelevantKind());
        commodityVO.setMinimumAmount(commodity.getMinimumAmount().doubleValue());
        commodityVO.setMaximumAmount(commodity.getMaximumAmount().doubleValue());
        commodityVO.setTradePrice(commodity.getCurrencyPrice().doubleValue());
        commodityVO.setLeaveMessage(commodity.getLeaveMessage());
        commodityVO.setCountry(commodity.getCountry());
        commodityVO.setCommodityTypeValue(commodity.getCommodityType());
        commodityVO.setTradeTimeValue(commodity.getTradeTimeType());
        commodityVO.setCurrencyAmount(commodity.getCurrencyQuantity().doubleValue());
        commodityVO.setTradeMethodValue(commodity.getTradeMethod());
        try {
            commodityVO.setTradeCurb(CommodityExtendVO.TradeCurbDTO.parseFrom(commodity.getTradeCurb().getBytes()));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        JSONArray payMethodArray = JSONArray.parseArray(commodity.getPayMethodList());
        Payaccount.PayMethodVO.Builder payMethodVO;
        List<Payaccount.PayMethodVO> list = new ArrayList<>();
        for (int i = 0;i < payMethodArray.size(); i++){
            payMethodVO =  Payaccount.PayMethodVO.newBuilder();
            Long userAccountId = Long.valueOf(payMethodArray.get(i).toString());
            UserAccount userAccount = userAccountService.getUserAccount(userAccountId);
            if (!Objects.isNull(userAccount)){
                payMethodVO.setAddress(userAccount.getAddress());
                if (PayMethodEnums.BANK_CARD.getValue() == userAccount.getPayType()){
                    payMethodVO.setBankName(userAccount.getPaymentDetail().split(":")[0]);
                    payMethodVO.setBankBranchName(userAccount.getPaymentDetail().split(":")[1]);
                }
                payMethodVO.setUserAccountId(userAccountId);
                payMethodVO.setAccount(userAccount.getAccount());
                payMethodVO.setRealName(userAccount.getRealName());
                payMethodVO.setPayTypeValue(userAccount.getPayType());
                payMethodVO.setPayStatusValue(userAccount.getPayStatus());
                list.add(payMethodVO.build());
            }
        }
        commodityVO.addAllPayMethod(list);

        //返回数据
        CommodityExtendVO.CommodityVOResult commodityVOResult = CommodityExtendVO.CommodityVOResult.getDefaultInstance();
        return  ProtoUtils.createResultSuccess(commodityVO.build(), commodityVOResult);
    }

    /**
     * @param commodityId
     * @return
     */
    @ApiOperation(value = "商品是否有效: true 商品有效  false 商品无效 ")
    @RequestMapping(value = "/validate-commodity", method = RequestMethod.GET)
    public Result<Boolean> validateCommodity(@ApiParam("商品id")@RequestParam(value = "commodityId") Long commodityId,
                                             @ApiParam("版本号")@RequestParam(value = "versionNumber") Integer versionNumber){

        Long userId = LoginDataHelper.getUserId();

        Boolean result = commodityService.validateCommodity(commodityId, userId,versionNumber);
        return  Result.createSuccess(result);
    }
}
