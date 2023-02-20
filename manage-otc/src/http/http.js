/**
 * Created by vikey on 2017/11/1.
 */
/**
 * http配置
 * * */
// 引入axios
import axios from 'axios'
import Cookies from 'js-cookie';
// axios 配置
axios.defaults.timeout = 5000;

//添加请求拦截器

// http request 拦截器
axios.interceptors.request.use(
  config => {

    if(config.url.indexOf('/file-upload/upload-pic-base64')>0)
    {
        axios.defaults.timeout = 5000*60;
        axios.defaults.maxContentLength = 200000000000000000000000000000;
    }
    //else

    // if(config.url.indexOf('-delete')>0){
    //   config.headers['Content-Type'] = 'application/x-www-form-urlencoded';
    // }else {
    //
    //
    //   console.log(JSON.stringify(config));
    //   let data = config.data;
    //   if(data.hasOwnProperty('postType')){
    //     config.headers['Content-Type'] = 'application/x-www-form-urlencoded';
    //   }else{
    //     config.headers['Content-Type'] = 'application/json';
    //   }
    //   console.log('====================================');
    //
    //
    //
    // }

    let token = Cookies.get('token');
    if(token != undefined){
      config.headers.token = token;
    }


    config.headers.applicationId = 4;
    config.headers.applicationClientType = 1;
    config.headers.applicationVersion = 1.4;
    config.headers.deviceUUID = '';
    return config;
  },
  err => {
    return Promise.reject(err);
  });


export default axios;
