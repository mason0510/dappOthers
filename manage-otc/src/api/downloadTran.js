import axios from 'axios';
import {baseUrl} from './baseUrl'
var qs = require('qs');

let base = baseUrl;
// let base = "http://127.0.0.1:8800";

//下载USTD未签名交易
export const download = params => {
  return axios({
    method:'get',
    url:`${base}/otc/otc-rest/m/download-transaction`,
    // url:`${base}/otc-rest/m/download-transaction`,
    timeout:30000,
    data:params,
    responseType: 'blob'
  })
};

//下载USTD提币未签名交易
export const downloadTixian = params => {
  return axios({
    method:'get',
    url:`${base}/otc/otc-rest/m/download-withdraw-transaction`,
    // url:`${base}/otc-rest/m/download-withdraw-transaction`,
    timeout:30000,
    params:params,
    responseType: 'blob'
  })
};

//下载ERC20未签名交易
export const downloadErc20 = params => {
  return axios({
    method:'get',
    url:`${base}/otc/otc-rest/m/download-erc20-collect-transaction`,
    // url:`${base}/otc-rest/m/download-erc20-collect-transaction`,
    timeout:30000,
    data:params,
    responseType: 'blob'
  })
};

//下载ERC20提币未签名交易
export const downloadErc20Tixian = params => {
  return axios({
    method:'get',
    url:`${base}/otc/otc-rest/m/download-erc20-withdraw-transaction`,
    // url:`${base}/otc-rest/m/download-erc20-withdraw-transaction`,
    timeout:30000,
    params:params,
    responseType: 'blob'
  })
};

//下载ERC20币种类型
export const currencyERC20List = params => {
  return axios({
    method:'get',
    url:`${base}/otc/otc-rest/m/currencyERC20List`,
    // url:`${base}/otc-rest/m/currencyERC20List`,
    timeout:30000,
    params:params
  })
};
