var moment = require('moment')

export const systemLogColumns = [
  {
    title: '序号',
    type: 'index',
    width: 80,
    align: 'center'
  },
  {
    title: '基础信息',
    width: 280,
    render: (h, params) => {
      const row = params.row;

      let color = "green";
      switch (row.logLevel){
        case "WARN":
          color="yellow";
          break;
        case "ERROR":
          color="red";
          break;
        default:
          //color=""
      }

      let date = moment(new Date(row.logTime)).format("MM-DD HH:mm:ss");



      let serviceName = row.serverName;

      return h('div', [
        h('Tag', {
          props: {
            type: 'dot',
            color: color
          }
        }, serviceName  + '  ' + date),
        h('p',  row.currentThread),
        h('p',  row.clazzName)
      ]);
    }
  },
  {
    title: '日志内容',
    align: 'left',
    key: 'logContent'
  }
  // ,
  // {
  //   title: '操作',
  //   align: 'center',
  //   width: 100,
  //   key: 'handle',
  //   handle: ['delete']
  // }
];

export const requestLogColumns = [
  {
    title: '序号',
    type: 'index',
    width: 80,
    align: 'center'
  },
  {
    title: '基础信息',
    width: 280,
    render: (h, params) => {
      const row = params.row;

      let color = "green";

      if(row.elapsedTime<0){
        color = "red";
      }else if(row.elapsedTime<1000){
        color = "green";
      }else if(row.elapsedTime<3000){
        color="yellow";
      }else{
        color="blue";
      }

      let date = moment(new Date(row.requestTime)).format("MM-DD HH:mm:ss");


      let clientType = '';
      switch (row.applicationClientType){
        case "1":
          clientType="Web";
          break;
        case "2":
          clientType="iOS";
          break;
        case "3":
          clientType="Android";
          break;
        default:

      }

      let serviceName = '【' + row.elapsedTime + '】 ' + row.serverName + ' ' + clientType;

      return h('div', [
        h('Tag', {
          props: {
            type: 'dot',
            color: color
          }
        }, serviceName  + '  ' + date),
        h('p',  row.method  + row.requestParam),
        h('p',  (row.applicationVersion ? row.applicationVersion : '')  + (row.requestIp ? (' - ' + row.requestIp)  : ''))
      ]);
    }
  },
  {
    title: '日志内容',
    align: 'left',
    key: 'responseResult'
  }
  // ,
  // {
  //   title: '操作',
  //   align: 'center',
  //   width: 100,
  //   key: 'handle',
  //   handle: ['delete']
  // }
];

const tableColumns = {
  systemLogColumns: systemLogColumns,
  requestLogColumns: requestLogColumns
};
export default tableColumns;
