import axios from 'axios';
import {baseUrl} from './baseUrl'
var qs = require('qs');

let base = baseUrl;

//获取评论列表
export const getCommentList = params => { return axios.post(`${base}/oauth/oauth-rest/manager/comment-list`, params); };

//评论审核
export const auditComment = params => { return axios.post(`${base}/oauth/oauth-rest/audit-comment`,qs.stringify(params) ); };
export const manageaddComment = params => { 
	return axios({//评论回复
		method:'post',
		url:`${base}/oauth/oauth-rest/manage/add-comment`,
		data:params
	})
};