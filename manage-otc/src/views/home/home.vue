<style lang="less">
    @import "./home.less";
    @import "../../styles/common.less";
</style>
<template>
    <div class="home-main">
        <Row>
            <Col span="8">
                <Row>
                  <Card>
                      <p slot="title" class="card-title">
                          <Icon type="android-map"></Icon>
                          今日新增房产数量
                      </p>
                      <div class="data-source-row">
                          <visite-volume :yAxisData="todaySaleAxisData" :seriesData="todaySaleSeriesData" elementId='todaySaleData'></visite-volume>
                      </div>
                  </Card>
                </Row>
                <Row class="margin-top-10">
                  <Card>
                      <p slot="title" class="card-title">
                          <Icon type="ios-shuffle-strong"></Icon>
                          可售房产数量
                      </p>
                      <div class="data-source-row">
                          <visite-volume :yAxisData="saleAxisData" :seriesData="saleSeriesData" elementId='saleData'></visite-volume>
                      </div>
                  </Card>
                </Row>
            </Col>
            <Col span="16" class-name="padding-left-5">
                <Row>
                  <Col span="6" >
                      <infor-card
                          id-name="transfer_count"
                          :end-val="count.transfer"
                          iconType="shuffle"
                          color="#f25e43"
                          intro-text="总计用户数量"
                      ></infor-card>
                  </Col>
                    <Col span="6" class-name="padding-left-5">
                        <infor-card
                            id-name="user_created_count"
                            :end-val="count.createUser"
                            iconType="android-person-add"
                            color="#2d8cf0"
                            intro-text="今日新增用户"
                        ></infor-card>
                    </Col>
                    <Col span="6" class-name="padding-left-5">
                        <infor-card
                            id-name="visit_count"
                            :end-val="count.uvCount"
                            iconType="ios-eye"
                            color="#64d572"
                            :iconSize="50"
                            intro-text="今日浏览人数"
                        ></infor-card>
                    </Col>
                    <Col span="6" class-name="padding-left-5">
                        <infor-card
                            id-name="collection_count"
                            :end-val="count.pvCount"
                            iconType="upload"
                            color="#ffd572"
                            intro-text="今日点击次数"
                        ></infor-card>
                    </Col>

                </Row>
                <Row class="margin-top-10">
                  <Col span="6" >
                      <infor-card
                          id-name="successCount"
                          :end-val="count.successCount"
                          iconType="checkmark-circled"
                          color="#f25e43"
                          intro-text="解析成功数"
                      ></infor-card>
                  </Col>
                    <Col span="6" class-name="padding-left-5">
                        <infor-card
                            id-name="successRatio"
                            :end-val="count.successRatio"
                            iconType="pie-graph"
                            color="#2d8cf0"
                            intro-text="解析成功率（%）"
                        ></infor-card>
                    </Col>
                    <Col span="6" class-name="padding-left-5">
                        <infor-card
                            id-name="crawler_count"
                            end-val="-"
                            iconType="thumbsup"
                            color="#64d572"
                            :iconSize="50"
                            intro-text="爬虫总数量"
                        ></infor-card>
                    </Col>
                    <Col span="6" class-name="padding-left-5">
                        <infor-card
                            id-name="pass_count"
                            end-val="-"
                            iconType="thumbsdown"
                            color="#ffd572"
                            intro-text="爬虫pass数量"
                        ></infor-card>
                    </Col>

                </Row>
                <Row class="margin-top-10">
                    <Card :padding="0" style="height:455px;">
                        <p slot="title" class="card-title">
                            <Icon type="map"></Icon>
                            服务调用地理分布
                        </p>
                        <div class="map-con">
                            <Col span="12">
                                <map-data-table :cityData="cityData" height="380" rightColumnName='今日人数' :style-obj="{margin: '12px 0 0 11px'}"></map-data-table>
                            </Col>
                            <Col span="12" class="map-incon">
                                  <map-data-table :cityData="weekCityData" height="380" rightColumnName='七天人数' :style-obj="{margin: '12px 11px 11px 11px'}"></map-data-table>
                            </Col>
                        </div>
                    </Card>
                </Row>
            </Col>
        </Row>
        <Row class="margin-top-10">
            <Col span="8">
                <Card>
                    <p slot="title" class="card-title">
                        <Icon type="android-map"></Icon>
                        上周每日来访量统计
                    </p>
                    <div class="data-source-row">
                        <visite-volume :yAxisData="weekSaleAxisData" :seriesData="weekSaleSeriesData" elementId='weekSaleData'></visite-volume>
                    </div>
                </Card>
            </Col>
            <Col span="8" class="padding-left-10">
                <Card>
                    <p slot="title" class="card-title">
                        <Icon type="ios-pulse-strong"></Icon>
                        服务访问人群分析
                    </p>
                    <div class="data-source-row">
                        <data-source-pie elementId="countDataSource" :legendData="pieLegendData" :seriesData="pieSeriesData"></data-source-pie>
                    </div>
                </Card>
            </Col>
            <Col span="8" class="padding-left-10">
                <Card>
                    <p slot="title" class="card-title">
                        <Icon type="android-wifi"></Icon>
                        服务访问内容分析
                    </p>
                    <div class="data-source-row" >
                        <!-- <user-flow></user-flow> -->
                        <Badge :count="unCheckCommentNum" style="margin:20px;">
                          <Button>待审核</br>评论数</Button>
                        </Badge>
                    </div>
                </Card>
            </Col>
        </Row>
        <Row class="margin-top-10">
            <Card>
                <p slot="title" class="card-title">
                    <Icon type="ios-shuffle-strong"></Icon>
                    上周每日服务调用量
                </p>
                <div class="line-chart-con">
                    <service-requests elementId="service_request_con" :legendData="lineLegendData" :seriesData="lineSeriesData"></service-requests>
                </div>
            </Card>
        </Row>
    </div>
</template>

<script>
// import cityData from './map-data/get-city-value.js';
import homeMap from './components/map.vue';
import dataSourcePie from './components/dataSourcePie.vue';
import visiteVolume from './components/visiteVolume.vue';
import serviceRequests from './components/serviceRequests.vue';
import userFlow from './components/userFlow.vue';
import countUp from './components/countUp.vue';
import inforCard from './components/inforCard.vue';
import mapDataTable from './components/mapDataTable.vue';
import toDoListItem from './components/toDoListItem.vue';

import {getGeoDistribution,getPVCount,getUVCount,getDataSourceCount,getLastWeekPV,getLastWeekServerPV,getSaleNum,getSaleNumForToday,getUserNumForToday,getUserNum,getSuccessCount,getSuccessRatio} from '../../api/analysis'

import {getCommentList} from '../../api/comment'

//引入JS
var moment = require('moment')
import {getSystemLogList} from '../../api/logs'

export default {
    name: 'home',
    components: {
        homeMap,
        dataSourcePie,
        visiteVolume,
        serviceRequests,
        userFlow,
        countUp,
        inforCard,
        mapDataTable,
        toDoListItem
    },
    data () {
        return {
            toDoList: [
            ],
            count: {
                createUser: 0,
                visit: 0,
                uvCount: 0,
                pvCount: 0,
                collection: 0,
                transfer: 0,
                successRatio:0,
                successCount:0
            },
            beforeDate : moment(new Date()).format("YYYY-MM-DD") + ' 00:00:00' ,
            afterDate : moment(new Date()).format("YYYY-MM-DD") + ' 23:59:59',
            applicationId : 2,
            cityData: [],
            weekCityData: [],
            ////访问客户端分类
            pieLegendData:['ios', 'android', 'web'],
            pieSeriesData:[{value: 0, name: 'ios', itemStyle: {normal: {color: '#9bd598'}}},
                      {value: 0, name: 'android', itemStyle: {normal: {color: '#ffd58f'}}},
                      {value: 0, name: 'web', itemStyle: {normal: {color: '#ab8df2'}}}],
            /// 上周每日服务访问量
            lineLegendData:[],
            lineSeriesData:[],
            //// 可销售房源数量
            saleAxisData:[],
            saleSeriesData:[],
            //// 今日可销售房源数量
            todaySaleAxisData:[],
            todaySaleSeriesData:[],
            //// 上周每日来访量
            weekSaleAxisData:[],
            weekSaleSeriesData:[],
            ///待审核数量
            unCheckCommentNum:0,
        };
    },
    computed: {
        avatorPath () {
            return localStorage.avatorImgPath;
        }
    },
    methods: {

       getHomePageInfo(){
         // 读取今日访问地理信息
         let para = {
           applicationId : this.applicationId,
           beforeDate : this.beforeDate ,
           afterDate : this.afterDate
         }
         getGeoDistribution(para).then((res) => {
           if(res.status==200){
             if(res.data.code==0){
               //toDoList
                //console.log(res.data.data);
                if(res.data.data){
                  for(let city of res.data.data){
                    let cityName  = '海外';
                    if(city.address!='null'){
                      cityName = city.address
                    }
                    this.cityData.push({name: cityName, value: city.count})
                  }
                }
               }
             }
         });

         // 读取七日日访问地理信息
         let curDate = new Date(this.beforeDate);
         let beforeWeekDate = new Date((curDate/1000-86400*6)*1000);
         para = {
           applicationId : this.applicationId,
           beforeDate : moment(beforeWeekDate).format("YYYY-MM-DD") + ' 00:00:00' ,
           afterDate : this.afterDate
         }
         getGeoDistribution(para).then((res) => {
           if(res.status==200){
             if(res.data.code==0){

                if(res.data.data){
                  for(let city of res.data.data){
                    let cityName  = '海外';
                    if(city.address!='null'){
                      cityName = city.address
                    }
                    this.weekCityData.push({name: cityName, value: city.count})
                  }
                }
               }
             }
         });

         // 今日新增用户数
        para = {
           applicationId : this.applicationId,
           beforeDate : this.beforeDate ,
           afterDate : this.afterDate
         }

         getUserNumForToday(para).then((res) => {
           if(res.status==200){
             if(res.data.code==0){
                this.count.createUser = res.data.data;
               }
             }
         });

         //用户总数
         para = {}
         getUserNum(para).then((res) => {
           if(res.status==200){
             if(res.data.code==0){
               //toDoList
                this.count.transfer = res.data.data;
               }
             }
         });

         // PV
        para = {
           applicationId : this.applicationId,
           beforeDate : this.beforeDate ,
           afterDate : this.afterDate
         }
         getPVCount(para).then((res) => {
           if(res.status==200){
             if(res.data.code==0){
                this.count.pvCount = res.data.data;
               }
             }
         });

        // UV
        para = {
           applicationId : this.applicationId,
           beforeDate : this.beforeDate ,
           afterDate : this.afterDate
         }
         getUVCount(para).then((res) => {
           if(res.status==200){
             if(res.data.code==0){
                this.count.uvCount = res.data.data;
               }
             }
         });

         // 数据来源统计
         para = {
            applicationId : this.applicationId,
            beforeDate : this.beforeDate ,
            afterDate : this.afterDate
          }
          getDataSourceCount(para).then((res) => {
            if(res.status==200){
              if(res.data.code==0){
                if(res.data.data){
                  let data = res.data.data;
                  // 1-web 2-ios 3-android
                  let web = 0;
                  let ios = 0;
                  let android = 0;
                  if(data["1"]){
                    web = data["1"]
                  }
                  if(data["2"]){
                    ios = data["2"]
                  }
                  if(data["3"]){
                    android = data["3"]
                  }
                  this.pieSeriesData = [
                      {value: ios, name: 'ios', itemStyle: {normal: {color: '#9bd598'}}},
                      {value: android, name: 'android', itemStyle: {normal: {color: '#ffd58f'}}},
                      {value: web, name: 'web', itemStyle: {normal: {color: '#ab8df2'}}}
                  ]
                }
                }
              }
          });

         // 上周每日来访量
         getLastWeekPV({}).then((res) => {
           if(res.status==200){
             if(res.data.code==0){
               //toDoList
                if(res.data.data){
                  let data = res.data.data;

                  let SaleAxisData=[];
                  let SaleSeriesData=[];

                  for(let i =data.length -1;i>=0;i--){
                    let day = data[i];
                    SaleAxisData.push(moment(day.date).format("YY.MM.DD"));
                    SaleSeriesData.push({value: day.count, name: day.date, itemStyle: {normal: {color: '#ab8df2'}}})
                    //SaleSeriesData.push(day.count);
                  }


                  this.weekSaleAxisData = SaleAxisData;
                  this.weekSaleSeriesData = SaleSeriesData;

                }

               }
             }
         });

         // 上周每日点击量
         getLastWeekServerPV({}).then((res) => {
           if(res.status==200){
             if(res.data.code==0){
               //toDoList
                if(res.data.data){
                  let data = res.data.data;
                  this.lineLegendData=[];
                  this.lineSeriesData=[];


                  let houseServer = {
                      name: '房产',
                      type: 'line',
                      stack: '次数',
                      areaStyle: {normal: {
                          color: '#2d8cf0'
                      }},
                      data: []
                  };

                  let oauthServer = {
                      name: '用户',
                      type: 'line',
                      stack: '次数',
                      areaStyle: {normal: {
                          color: '#10A6FF'
                      }},
                      data: []
                  };

                  let knowledgeServer = {
                      name: '资讯',
                      type: 'line',
                      stack: '次数',
                      areaStyle: {normal: {
                          color: '#0C17A6'
                      }},
                      data: []
                  };

                  let total = {
                      name: '点击总数',
                      type: 'line',
                      stack: '次数',
                      label: {
                          normal: {
                              show: true,
                              position: 'top'
                          }
                      },
                      areaStyle: {normal: {
                          color: '#398DBF'
                      }},
                      data: []
                  };

                  let LegendData = [];

                  for(let day of data){
                    LegendData.push(moment(day.date).format("YY.MM.DD"));
                    if(day.serverInfoVOList){
                      let totalNum = 0;
                      for(let service of day.serverInfoVOList){
                        if(service.serverName == 'house'){
                          houseServer.data.push(service.count);
                        }else if(service.serverName == 'oauth'){
                          oauthServer.data.push(service.count);
                        }else if(service.serverName == 'knowledge'){
                          knowledgeServer.data.push(service.count);
                        }
                        totalNum = totalNum + service.count;
                      }
                      total.data.push(totalNum);
                    }
                    // this.lineSeriesData.push(day.count);
                  }

                  //开始算总账
                  this.lineLegendData = LegendData;
                  this.lineSeriesData = [houseServer,oauthServer,knowledgeServer,total];

                }

               }
             }
         });

         //可售房产数量
         /*getSaleNum({}).then((res) => {
           if(res.status==200){
             if(res.data.code==0){

                if(res.data.data){
                  let yAxisData = [];
                  let seriesData = [];
                  for(let city of res.data.data){
                    yAxisData.push(city.regionName);
                    seriesData.push({value: city.num, name: city.regionName, itemStyle: {normal: {color: '#ffd58f'}}})
                  }
                  this.saleAxisData = yAxisData;
                  this.saleSeriesData = seriesData;
                }
               }
             }
         });*/

         //今日可售房产数量
        /* getSaleNumForToday({}).then((res) => {
           if(res.status==200){
             if(res.data.code==0){

                if(res.data.data){
                  let yAxisData = [];
                  let seriesData = [];
                  for(let city of res.data.data){
                    yAxisData.push(city.regionName);
                    seriesData.push({value: city.num, name: city.regionName, itemStyle: {normal: {color: '#9bd598'}}})
                  }
                  this.todaySaleAxisData = yAxisData;
                  this.todaySaleSeriesData = seriesData;
                }
               }
             }
         });*/
		let params = {
           beforeDate : moment(beforeWeekDate).format("YYYY-MM-DD") + ' 00:00:00' ,
           afterDate : this.afterDate
         }
		//解析成功数
         /*getSuccessCount(params).then((res) => {
           if(res.status==200){
             if(res.data.code==0){
                if(res.data.data){
                 this.count.successCount=res.data.data;
                }
               }
             }
         });*/
         
         //解析成功率
        /* getSuccessRatio(params).then((res) => {
           if(res.status==200){
             if(res.data.code==0){
                if(res.data.data){
                  this.count.successRatio=res.data.data;
                }
               }
             }
         });*/
       },




        getSystemLog(){

          let para = {
            afterDate : '',
            beforeDate : '',
            keywords : [],
            logLevel : 'ERROR',
            serverName : '',
            pageNo : 1,
            pageSize : 20
          }


          getSystemLogList(para).then((res) => {
            if(res.status==200){
              if(res.data.Code==0){
                //toDoList

                let arr = res.data.Data.dataList;

                this.toDoList = [];

                for(let i=0;i<arr.length;i++){
                  let logItem = arr[i];

                  let serviceName = logItem.serverName;
                  let date = moment(new Date(logItem.logTime)).format("MM.DD HH:mm:ss") + ' ' + logItem.clazzName;
                  //+ '[' + serviceName + ']'
                  let toDoItem = {
                    title: date
                  };

                  this.toDoList.push(toDoItem);

                }


              }else{
//                if(res.data.Message){
//                  this.$Message.error(res.data.Message);
//                }
              }
            }else{
//              this.$Message.error('接口读取失败!');
            }

          });
        },
        addNew () {
            if (this.newToDoItemValue.length !== 0) {
                this.toDoList.unshift({
                    title: this.newToDoItemValue
                });
                setTimeout(function () {
                    this.newToDoItemValue = '';
                }, 200);
                this.showAddNewTodo = false;
            } else {
                this.$Message.error('请输入待办事项内容');
            }
        },
        cancelAdd () {
            this.showAddNewTodo = false;
            this.newToDoItemValue = '';
        },

        getUnCheckNum(){

          let para = {
            auditStatus : 1,
            refType : '',
            pageIndex : 1,
            pageSize : 1
          }
          getCommentList(para).then((res) => {
            if(res.status==200){
              if(res.data.code==0){
                this.unCheckCommentNum = res.data.totalResults;
              }
            }
          });
        }



    },
    mounted () {
     this.getSystemLog();
       this.getHomePageInfo();
      this.getUnCheckNum();
    }
};
</script>
