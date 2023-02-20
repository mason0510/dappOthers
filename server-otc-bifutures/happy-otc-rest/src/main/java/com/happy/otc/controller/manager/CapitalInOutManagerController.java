package com.happy.otc.controller.manager;

import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.bitan.common.vo.PageResult;
import com.bitan.common.vo.Result;
import com.bitan.oauth.vo.UserInfoVO;
import com.github.pagehelper.PageInfo;
import com.happy.otc.dto.CapitalInOutDTO;
import com.happy.otc.dto.UserDTO;
import com.happy.otc.entity.CapitalInOut;
import com.happy.otc.entity.Currency;
import com.happy.otc.enums.CapitalInOutTypeEnum;
import com.happy.otc.service.*;
import com.happy.otc.service.remote.IOauthService;
import com.happy.otc.util.BeanUtils;
import com.happy.otc.util.FileZipUtil;
import com.happy.otc.vo.CapitalInOutVO;
import com.happy.otc.vo.manager.CapitalInOutSearchVO;
import com.happy.otc.vo.manager.WithdrawalsRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "otc-rest/m")
@Api(value = "otc-rest/m", description = "后台管理 提币 API")
public class CapitalInOutManagerController {

    private final Logger logger = LoggerFactory.getLogger(CapitalInOutManagerController.class);

    @Autowired
    private ICapitalInOutService capitalInOutService;

    @Autowired
    private IOauthService oauthService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IOmniTransactionService omniTransactionService;
    @Autowired
    private IERC20TransactionService createWithdrawTransactionFile;
    @Autowired
    private ICurrencyService currencyService;
    @Value("${eth.tx.transaction}")
    private String txHashUrl;

    @ApiOperation(value = "提币列表")
    @PostMapping("/capital-out-list")
    public PageResult<CapitalInOutVO> capitalOutList(@RequestBody CapitalInOutSearchVO searchVO) {

        CapitalInOut capitalInOut = new CapitalInOut();
        capitalInOut.setType(CapitalInOutTypeEnum.CAPITAL_OUT.getValue());        //类别 1:充币 2：提币
        capitalInOut.setStatus(searchVO.getStatus());
        //根据手机号获取userId
        if (StringUtils.isNotBlank(searchVO.getMobile())) {
            Result<UserInfoVO> userInfoVOResult = oauthService.searchUserByMobile(searchVO.getMobile().trim());
            if (userInfoVOResult.isSuccess() && userInfoVOResult.getData() != null) {
                capitalInOut.setUserId(userInfoVOResult.getData().getUserId());
            }
        }
        PageInfo<CapitalInOutDTO> pageList = capitalInOutService.getCapitalInOutList(capitalInOut, searchVO.getCurrentPage(), searchVO.getPageSize());

        //获取用户信息
        List<Long> userIds = new ArrayList<>();
        for(CapitalInOutDTO item : pageList.getList()){
            userIds.add(item.getUserId());
        }
//        Result<Map<Long, UserInfoVO>> userList = oauthService.getUserInfoByUserIdList(userIds);
        Map<Long, UserDTO> userDTOMap = userService.UserInfoByUserIds(userIds);

        List<CapitalInOutVO> voList =  BeanUtils.copyObjectList(pageList.getList(), CapitalInOutVO.class);

        //赋值用户名称
        for (CapitalInOutVO item : voList) {
            UserDTO userDTO = userDTOMap.get(item.getUserId());
            item.setUserName(userDTO.getUserName());
            item.setMobile(userDTO.getMobile());
        }
        return new PageResult(voList, pageList.getPageNum()
                , pageList.getPageSize(), pageList.getTotal());
    }

    @ApiOperation(value = "执行发币")
    @PostMapping("/withdrawals")
    public Result<Boolean> withdrawals(@RequestBody WithdrawalsRequest request){

        Boolean result = capitalInOutService.withdrawals(request.getCapitalInOutId());
        return Result.createSuccess(result);
    }

    @ApiOperation(value = "下载管理未签名USTD交易，集中至同一个地址")
    @GetMapping("/download-transaction")
    public void downloadTransaction(HttpServletResponse res) throws Throwable {
        logger.info("下载管理签名开始");
        //创建未签名交易文件
        String filePath = omniTransactionService.createTransactionFile();
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        outFile(res,fileName,filePath);
        logger.info("下载管理签名结束");

    }

    @ApiOperation(value = "下载提现未签名USTD交易,用来提现")
    @GetMapping("/download-withdraw-transaction")
    public void downloadWithdrawTransaction(HttpServletResponse res
            ,@RequestParam(value = "withdrawAddress") String withdrawAddress,
                @RequestParam(value = "amount") String amount) throws Throwable {

        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        if (!pattern.matcher(amount).matches()){
            BizException.fail( ApiResponseCode.PARA_ERR,"参数错误");
        }
        logger.info("下载提现签名开始");
        //创建未签名交易文件
        String filePath = omniTransactionService.createWithdrawTransactionFile(withdrawAddress,amount);
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        outFile(res,fileName,filePath);
        logger.info("下载提现签名结束");

    }

    @ApiOperation(value = "下载管理未签名ETH交易，从0号地址给地址充值")
    @GetMapping("/download-erc20-withdraw-transaction")
    public void downloadERC20WithdrawTransaction(
            HttpServletResponse res,
            @RequestParam(value = "withdrawAddress") String withdrawAddress,
            @RequestParam(value = "ethAmount") Double ethAmount,
            @RequestParam(value = "currencyName") String currencyName) throws Throwable {
        logger.info("下载ERC20提现签名开始");

       Currency currency =  currencyService.getCurrency(currencyName);
        if (currency == null){
           BizException.fail( ApiResponseCode.PARA_ERR, "币种不存在" );
        }

        if (withdrawAddress.startsWith( "0x" )){
           withdrawAddress = withdrawAddress.substring( 2 );
        }
        //创建未签名交易文件
        String filePath = createWithdrawTransactionFile.createWithdrawTransactionFile(withdrawAddress, ethAmount, currency);
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        outFile(res,fileName,filePath);
        logger.info("下载ERC20提现签名结束");
    }


    @ApiOperation(value = "下载管理未签名ERC20交易，聚集到0号地址")
    @GetMapping("/download-erc20-collect-transaction")
    public void downloadERC20CollectTransaction(
            HttpServletResponse res) throws Throwable {
        logger.info("下载ERC20聚集签名开始");
        //创建未签名交易文件
        String filePath = createWithdrawTransactionFile.createTransactionFile();
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        outFile(res,fileName,filePath);
        logger.info("下载ERC20聚集签名结束");
    }

    @ApiOperation(value = "能进行签名交易的币种")
    @GetMapping("/currencyERC20List")
    public Result<List<String>> currencyERC20List() {
        List<Currency> list =  currencyService.getCurrencyList();

        List<String> ercList = new ArrayList<>();
        list.forEach( v -> {
            if (!v.getCurrencySimpleName().equals("BTC") &&!v.getCurrencySimpleName().equals("USDT")){
                ercList.add(v.getCurrencySimpleName());
            }
        } );

        return Result.createSuccess(ercList);
    }


    @ApiOperation(value = "对签好的交易进行广播")
    @PostMapping("/ethSendRawTransaction")
    public void ethSendRawTransaction(HttpServletResponse res, @RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "currencyName") String currencyName) throws Throwable {

        if (file.isEmpty()){
            BizException.fail( ApiResponseCode.PARA_NIL,"上传失败，请选择文件。" );
        }

        Currency currency =  currencyService.getCurrency(currencyName);
        if (currency == null){
            BizException.fail( ApiResponseCode.PARA_ERR, "币种不存在" );
        }

        BufferedReader bReader = null;
        File readFile = null;
        List<String> list = new ArrayList<>();
        //创建工作目录
        String workPath = System.getProperty("user.dir") + File.separator + "work" + File.separator ;
        try {

            readFile = new File(workPath+"\\11.txt");

            file.transferTo(readFile);
            //文件内容传输
            bReader = new BufferedReader(new FileReader(readFile));

            // 一行一行的写
            String strLine = null;
            while ( (strLine = bReader.readLine()) != null){
                strLine = createWithdrawTransactionFile.ethSendRawTransaction(strLine,currency);
                if (strLine != null){
                    list.add(txHashUrl + strLine)  ;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            //BizException.fail( ApiResponseCode.PARA_NIL,"上传失败，请选择文件。" );
        }finally {
            if (bReader != null){
                bReader.close();
            }
            if (readFile != null){
                FileZipUtil.deleteDir(readFile);
            }
        }

        //没有交易内容
        if (list.isEmpty()){
            BizException.fail( ApiResponseCode.PARA_NIL,"上传文件内容不对。" );
        }

        File workFolder = new File(workPath);
        if (!workFolder.exists() && !workFolder.isDirectory()) {
            workFolder.mkdirs();
        }

        //创建文件
        String fileName = "txhash"+ "_"+ System.currentTimeMillis() + ".csv";

        String filePath = createWithdrawTransactionFile.createTxhashFile(list,workPath,fileName);
        outFile(res,fileName,filePath);
    }
    /**
     * 根据路径，指定文件输出
     * @param res
     * @param fileName
     * @param filePath
     */
    public void outFile(HttpServletResponse res,String fileName,String filePath){
        res.reset();
        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        res.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = res.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(new File(filePath)));
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
