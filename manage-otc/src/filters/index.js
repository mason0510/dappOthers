let documentType = val => {  //证件类型 0:身份证 1:护照
  if (val === 0) {
    return '身份证'
  } else if (val === 1) {
    return '护照'
  }
}

let statusType = val => {  //认证状态 0:未认证 1:待认证 2:已认证 3:认证驳回
  if (val === 0) {
    return '未认证'
  } else if (val === 1) {
    return '待认证'
  } else if (val === 2) {
    return '已认证'
  } else if (val === 3) {
    return '认证驳回'
  }
}

let appealStatus = val => {  //申诉状态 0：处理中，1：胜诉 ，2:败诉  3:取消
  if (val === 0) {
    return '处理中'
  } else if (val === 1) {
    return '胜诉'
  } else if (val === 2) {
    return '败诉'
  } else if (val === 3) {
    return '取消'
  }
}

let appealType = val => {  //申诉类型 1:对方未付款 2:对方未放行 3:对方无应答 4:对方有欺诈行为 5:其他
  if (val === 1) {
    return '对方未付款'
  } else if (val === 2) {
    return '对方未放行'
  } else if (val === 3) {
    return '对方无应答'
  } else if (val === 4) {
    return '对方有欺诈行为'
  } else if (val === 5) {
    return '其他'
  }
}

let capitalOutStatus = val => {  //提币状态:0:审核中 1：成功 2：失败
  if (val === 0) {
    return '审核中'
  } else if (val === 1) {
    return '成功'
  } else {
    return '失败'
  }
}

let commodityType = val => {  //商品类别 0：在买 1：在卖
  if (val === 0) {
    return '在买'
  } else if (val === 1) {
    return '在卖'
  }
}

let houseStatus = val => {  //房屋状态
    if(val===1){
        return '已下架/已售'
    }  else if(val===2){
        return '审核中'
    }  else if(val===3){
    	 return '已过委托期'
    }  else if(val===4){
    	 return '热卖中 '
    }
}
let infoType = val => {  //资讯类型
    if(val===3){
        return '攻略'
    }  else if(val===1){
        return '快讯'
    }  else if(val===2){
    	 return '要闻'
    }
}
let infoLang = val => {  //资讯语种
    if(val==='cn'){
        return '中文'
    }  else if(val==='en'){
        return '英文'
    }
}
let timeFormat= val => {
       var date =  new Date(val);
    var y = 1900+date.getYear();
    var m = "0"+(date.getMonth()+1);
    var d = "0"+date.getDate();
    return y+"-"+m.substring(m.length-2,m.length)+"-"+d.substring(d.length-2,d.length);
}
export {documentType, statusType, appealStatus, appealType, capitalOutStatus, commodityType, timeFormat}
