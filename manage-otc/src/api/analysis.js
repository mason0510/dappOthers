import axios from 'axios';
import {baseUrl} from './baseUrl'
var qs = require('qs');
let base = baseUrl;


//访问者地理分布图
export const getGeoDistribution = params => { return axios.post(`${base}/log/log-rest/service-geo-distribution`,params); };

//浏览量 PV
export const getPVCount = params => { return axios.post(`${base}/log/log-rest/count-pv`,params); };

//浏览量 UV
export const getUVCount = params => { return axios.post(`${base}/log/log-rest/count-uv`,qs.stringify(params)); };

//数据来源统计
export const getDataSourceCount = params => { return axios.post(`${base}/log/log-rest/count-data-source`,params); };

//上周每日PV
export const getLastWeekPV = params => { return axios.get(`${base}/log/log-rest/count-lastweek-request`); };

//上周服务PV
export const getLastWeekServerPV = params => { return axios.get(`${base}/log/log-rest/count-lastweek-server-request`); };

//可售房产数量
export const getSaleNum = params => { return axios.get(`${base}/house/house/m/statistics/saleNum`); };

//今日新增可售房产数量
export const getSaleNumForToday = params => { return axios.get(`${base}/house/house/m/statistics/toadyIncreased`); };

//今日新增注册数
export const getUserNumForToday = params => { return axios.post(`${base}/oauth/oauth-rest/count-register`,params); };

//用户总数
export const getUserNum = params => { return axios.get(`${base}/oauth/oauth-rest/count-all-user`); };

//解析成功数
export const getSuccessCount = params => { return axios.get(`${base}/aiui/aiui/success-count`); };

//解析成功率
export const getSuccessRatio = params => { return axios.get(`${base}/aiui/aiui/success-ratio`); };
