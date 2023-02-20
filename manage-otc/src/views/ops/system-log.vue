<template xmlns="http://www.w3.org/1999/html">
  <div>
    <Row :gutter="10">
      <Card>

        <p slot="title" style="height: 40px;line-height: 35px">

          <div style="float:left;padding-right:4px;">
            <tags-input :tags="keyWords" style="width: 200px;height:30px;margin-top:1px"  @tags-change="handleInputChange" ></tags-input>
          </div>
          <DatePicker type="datetimerange" @on-clear="handleClear" @on-change="handleChange" :value="value2" :options="options2" format="yyyy-MM-dd HH:mm:ss" placeholder="请选择起止时间" style="width: 250px"></DatePicker>
          <span >

            <!-- <Input v-model="keyWords"   placeholder="请输入关键字" style="width: 200px;padding-bottom: 4px" /> -->
          </span>
          <span>
            <Poptip placement="bottom" >
              <Button type="ghost">更多</Button>
                <div class="api" slot="content">
                  <Select v-model="level" style="width:200px" placeholder="选择等级">
                      <Option v-for="item in levels" :value="item.value" :key="item.value">{{ item.label }}</Option>
                  </Select>
                  <Select v-model="serviceName" style="width:200px" placeholder="选择服务">
                      <Option v-for="item in services" :value="item.value" :key="item.value">{{ item.label }}</Option>
                  </Select>
                </div>
            </Poptip>
          </span>
          <span ><Button  style="width:90px;"  icon="ios-search" type="ghost" @click="handleSearch">搜索</Button></span>
        </p>
        <p></p>
        <yztz-table :columns="columns"
                    :tableData="tableData"
                    :loading="loading"
                    :total="total"
                    :pageSize="pageSize"
                    :showOP="false"
                    viewText="客户端"
                    @changePage="changePage"
                    @editRow=editRow
                    @deleteRow="deleteRow"
                    @viewRow="viewRow"
                    @selectionChange=selectionChange
                    @pageSizeChange="pageSizeChange">

        </yztz-table>
      </Card>
    </Row>
  </div>
</template>

<script>

  import tableColumns from './columns.js';
  import {getSystemLogList} from '../../api/logs'


  export default {
    components: {
        'tags-input': require('vue-tagsInput')
    },
    data () {
      return {
        //日期
        options2: {
          shortcuts: [
            {
              text: '一周',
              value () {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                return [start, end];
              }
            },
            {
              text: '一月',
              value () {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                return [start, end];
              }
            },
            {
              text: '三月',
              value () {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                return [start, end];
              }
            }
          ]
        },
        value2:[
          '', ''
        ],
        //搜索
        keyWords : [],
        levels:[
          {
            value: '',
            label: '所有等级'
          },
          {
            value: 'ERROR',
            label: 'ERROR'
          },
          {
            value: 'WARN',
            label: 'WARN'
          },
          {
            value: 'INFO',
            label: 'INFO'
          }
        ],
        level:'',
        services:[
          {
            value: '',
            label: '所有服务'
          },
          {
            value: 'oauth',
            label: 'oauth'
          },
          {
            value: 'log',
            label: 'log'
          },
          {
            value: 'message',
            label: 'message'
          },
          {
            value: 'system',
            label: 'system'
          },
          {
            value: 'apigateway',
            label: 'apigateway'
          },
          {
            value: 'config',
            label: 'config'
          },
          {
            value: 'monitor',
            label: 'monitor'
          }
        ],
        serviceName:'',
        //table
        columns: [],
        loading: false,
        tableData: [],
        pageSize:20,
        pageIndex:1,
        total:0
      };
    },
    methods: {
      // 初始化信息
      getData () {

        this.loading = true;

        let afterDate = '';
        let beforeDate = '';
        if (this.value2.length > 1){
          if(this.value2[0] != ""){
            afterDate = this.value2[1];
            beforeDate = this.value2[0];
          }
        }

        let para = {
          afterDate : afterDate,
          beforeDate : beforeDate,
          keywords : this.keyWords,
          logLevel : this.level,
          serverName : this.serviceName,
          pageNo : this.pageIndex,
          pageSize : this.pageSize
        }

        getSystemLogList(para).then((res) => {
          if(res.status==200){
            if(res.data.code==0){
              this.columns = tableColumns.systemLogColumns;
              this.tableData=res.data.data.dataList;
              this.total = res.data.data.count;
            }else{
              if(res.data.resultMsg){
                this.$Message.error(res.data.resultMsg);
              }
            }
          }else{
            this.$Message.error('系统日志接口读取失败!');
          }


          this.loading = false;
        });

      },
      //日期
      handleChange (arrDate) {
       this.value2 = arrDate;
      },
      handleClear () {
        //
      },
      // Component Add-Panel
      handleSearch(){
        this.getData();
      },
      // 表格内容
      //表格行 选中后变化
      selectionChange(selection){
        alert(JSON.stringify(selection));
      },
      // 编辑表格
      editRow(row,index){

      },
      // 删除表格
      deleteRow(row,index){

        alert(JSON.stringify(row));

      },
      // 查看表格
      viewRow(row,index){

        alert(JSON.stringify(row));

      },
      // 表格翻页
      changePage (index) {
        // 这里直接更改了模拟的数据，真实使用场景应该从服务端获取数据
        this.pageIndex = index;
        this.getData();
      },
      // 表格每页数量切换
      pageSizeChange(pagesize){
        this.pageIndex = 1;
        this.pageSize = pagesize;
        this.getData();
      },
      //Model 处理
      handelSubmit(){

      },
      handelCancel(){

      },
      handleInputChange(index, text) {
        if (text) {
          this.keyWords.splice(index, 0, text)
        } else {
          this.keyWords.splice(index, 1)
        }
      }
    },
    computed: {

    },
    mounted () {
      this.getData();
    }
  };
</script>
