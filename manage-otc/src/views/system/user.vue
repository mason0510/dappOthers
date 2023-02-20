
<template>
  <div>
    <Row :gutter="10">
      <Card>
        <div style="float: left;margin-right: 6px;padding-top: 2px">
          <Select v-model="applicationId" @on-change="applicationChanged" style="width:200px">
            <Option v-for="item in applications" :value="item.applicationId" :key="item.applicationId">{{ item.name }}</Option>
          </Select>
          <Select v-model="userFrom"  style="width:200px"  placeholder="请选择来源">
            <Option v-for="item in userFromList" :value="item.value" :key="item.value">{{ item.label }}</Option>
          </Select>
          <DatePicker type="datetimerange" v-model="timeSection" format="yyyy-MM-dd" placeholder="请选择时间区间" style="width: 200px"></DatePicker>
        </div>

        <yztz-add-panel :searchTxt="userAccount"  placeholder="你好！" v-on:handleSearch="handleSearch" v-on:add="handleAdd"></yztz-add-panel>

        <yztz-table :columns="columns"
                    :tableData="tableData"
                    :loading="loading"
                    :total="total"
                    :pageSize="pageSize"
                    viewText="绑定"
                    @changePage="changePage"
                    @editRow=editRow
                    @deleteRow="deleteRow"
                    @viewRow="viewRow"
                    @selectionChange=selectionChange
                    @pageSizeChange="pageSizeChange">

        </yztz-table>
      </Card>

      <Modal
        v-model="showModal"
        :title="modalTitle" :mask-closable="false">
        <Form :label-width="80" :model="editForm" ref="editForm" :rules="editFormValidate">
          <FormItem label="用户名" prop="userName">
            <Input v-model="editForm.userName"  />
          </FormItem>

          <FormItem label="性别" >
            <RadioGroup v-model="editForm.gender">
              <Radio label="1" >男
              </Radio>
              <Radio label="2" >女
              </Radio>
              <Radio label="0" >未知
              </Radio>
            </RadioGroup>
          </FormItem>
          <FormItem label="邮件" prop="email">
            <Input v-model="editForm.email"  />
          </FormItem>
          <FormItem label="手机" prop="mobile">
            <Input v-model="editForm.mobile">
              <Select v-model="editForm.countryCode" slot="prepend" style="width: 80px">
                <Option value="+86">+86</Option>
                <Option value="+1">+1</Option>
              </Select>
            </Input>
          </FormItem>
          <div style="display: none">
            <Input v-model="editForm.userType"  />
          </div>
        </Form>
        <div slot="footer">
          <Button   @click="handelCancel">取消</Button>
          <Button type="primary"  @click="handelSubmit">提交</Button>
        </div>
      </Modal>
      <Modal
        v-model="showBindForm"
        title="应用绑定" :mask-closable="false">
        <Form :label-width="80" :model="bindForm" ref="bindForm" :rules="bindFormValidate" >
          <form-item>
            <Checkbox
              :indeterminate="indeterminate"
              :value="checkAll"
              @click.prevent.native="handleCheckAll">全选</Checkbox>
          </form-item>
          <form-item prop="bindApplicationIds"  >


            <CheckboxGroup v-model="bindForm.bindApplicationIds"  @on-change="checkAllGroupChange">

              <Checkbox v-for="item in applications" :label="item.applicationId" :key="item.applicationId">{{ item.name }}</Checkbox>

            </CheckboxGroup>
          </form-item>

        </Form>
        <div slot="footer">
          <Button   @click="showBindForm=false">取消</Button>
          <Button type="primary"  @click="handelBindSubmit">提交</Button>
        </div>
      </Modal>
      <Spin size="large" fix v-if="spinShow"></Spin>
    </Row>
  </div>
</template>

<script>

  import tableColumns from './columns.js';
  import {getApplicationList} from '../../api/application'
  import {addUser,getUserList,bindApplication} from '../../api/user'
  import FormItem from '../../../node_modules/iview/src/components/form/form-item.vue'
	var moment = require('moment')
  export default {
    components: {FormItem},
    data () {
      return {
      	timeSection:[],
      	userFrom:'',
      	userFromList:[{
      		label:'全部',
      		value:'',
      	},{
      		label:'APPSTORE',
      		value:'APPSTORE',
      	},{
      		label:'pc',
      		value:'pc',
      	},{
      		label:'yingyongbao',
      		value:'yingyongbao',
      	},{
      		label:'android360',
      		value:'android360',
      	},{
      		label:'xiaomi',
      		value:'xiaomi',
      	},{
      		label:'baidu',
      		value:'baidu',
      	},{
      		label:'ali',
      		value:'ali',
      	},{
      		label:'huawei',
      		value:'huawei',
      	},{
      		label:'meizu',
      		value:'meizu',
      	},{
      		label:'google',
      		value:'google',
      	},{
      		label:'sougou',
      		value:'sougou',
      	},{
      		label:'samsung',
      		value:'samsung',
      	},{
      		label:'leshi',
      		value:'leshi',
      	},{
      		label:'anzhi',
      		value:'anzhi',
      	},{
      		label:'chuizi',
      		value:'chuizi',
      	},{
      		label:'xunfei',
      		value:'xunfei',
      	},{
      		label:'anfen',
      		value:'anfen',
      	},{
      		label:'yingyonghui',
      		value:'yingyonghui',
      	},{
      		label:'woshop',
      		value:'woshop',
      	},{
      		label:'youyi',
      		value:'youyi',
      	},{
      		label:'mumayi',
      		value:'mumayi',
      	},{
      		label:'maopao',
      		value:'maopao',
      	},{
      		label:'lianxiang',
      		value:'lianxiang',
      	},{
      		label:'jinli',
      		value:'jinli',
      	},{
      		label:'leshangdian',
      		value:'leshangdian',
      	},{
      		label:'oppo',
      		value:'oppo',
      	},{
      		label:'vivo',
      		value:'vivo',
      	}],
        //applications
        applications:[],
        applicationId:0,
        //
        spinShow:false,
        //搜索
        userAccount : '',
        //table
        columns: [],
        loading: false,
        tableData: [],
        pageSize:20,
        pageIndex:1,
        total:0,
        //编辑
        showModal:false,
        modalTitle:'',
        //表单内容
        editFormValidate: {
          userName: [
            { required: true, message: '用户名不能为空', trigger: 'blur' }
          ],
          email: [
            { required: true, message: '邮件不能为空', trigger: 'blur' }
          ],
          mobile: [
            { required: true, message: '手机不能为空', trigger: 'blur' }
          ]
        },
        editForm:{
          userName:'',
          countryCode:'+86',
          email:'',
          gender:0,
          mobile:'',
          password:'',
          userType:0,
          applicationIdList:[0]
        },
        // 绑定
        showBindForm:false,
        indeterminate: true,
        checkAll: false,
        bindFormValidate: {
          bindApplicationIds: [
            { required: true, type: 'array', min: 1, message: '至少所属一个应用', trigger: 'change' }
          ]
        },
        bindApplications:[],
        bindForm:{
          userId:0,
          bindApplicationIds:[]
        }
      };
    },
    methods: {
      // 初始化信息
      getApplicationData () {

        getApplicationList().then((res) => {
          if(res.status==200){
            if(res.data.code==0){

              this.applications=res.data.data;

              if(this.applications.length>0){

                this.applicationId = 3;//this.applications[0].applicationId;
              }

            }else{
              if(res.data.Message){
                this.$Message.error(res.data.Message);
              }
            }
          }else{
            this.$Message.error('接口读取失败!');
          }

        });

      },
      getUserData () {

        this.loading = true;
        let para = {
          pageIndex: this.pageIndex,
          pageSize: this.pageSize,
          applicationId: this.applicationId,
          userAccount: this.userAccount,
          userFrom:this.userFrom
        };
			if(this.timeSection[0]){
				let startTime = moment(new Date(this.timeSection[0])).format("YYYY-MM-DD HH:mm:ss");
				let endTime = moment(new Date(this.timeSection[1])).format("YYYY-MM-DD HH:mm:ss");
        	para.startTime=startTime
        	para.endTime=endTime
        }else{
        	para.startTime=''
        	para.endTime=''
        }
        getUserList(para).then((res) => {
          if(res.status==200){
            if(res.data.code==0){
              this.columns = tableColumns.userColumns;
              if(res.data.data){
                this.tableData=res.data.data;
              }else{
                this.tableData=[];
              }

              this.total = res.data.totalResults;
            }else{
              if(res.data.Message){
                this.$Message.error(res.data.Message);
              }
            }
          }else{
            this.$Message.error('接口读取失败!');
          }


          this.loading = false;
        });

      },
      applicationChanged(e){
        this.pageIndex = 1;
        this.getUserData()
      },

      // Component Add-Panel
      handleSearch(keywords){
        this.pageIndex = 1;
        this.userAccount = keywords;
        this.getUserData();
      },
      handleAdd(){
        this.modalTitle = "新增-用户";
        this.showModal = true;
        this.editForm={
          userName:'',
          countryCode:'+86',
          email:'',
          gender:0,
          mobile:'',
          password:'',
          userType:0,
          applicationIdList:[this.applicationId]
        }
      },
      // 表格内容
      //表格行 选中后变化
      selectionChange(selection){
        alert(JSON.stringify(selection));
      },
      // 编辑表格
      editRow(row,index){
        this.modalTitle = "新增-用户";
        this.showModal = true;
        this.editForm=Object.assign({}, row);
        this.editForm.applicationIdList = [this.applicationId];
      },
      // 删除表格
      deleteRow(row,index){

        alert(JSON.stringify(row));

      },
      // 查看表格
      viewRow(row,index){

        let para = {
          pageIndex: 1,
          pageSize: 20,
          userId:row.userId
        };

        getUserList(para).then((res) => {
          if(res.status==200){
            if(res.data.code==0){
              this.bindForm.bindApplicationIds = res.data.data.map(v => v.applicationId);
            }else{
              this.bindForm.bindApplicationIds = [this.applicationId];
            }
          }else{
            this.bindForm.bindApplicationIds = [this.applicationId];
          }

        });

        //alert(JSON.stringify(row));

        this.bindForm.userId = row.userId;
        this.showBindForm = true;

      },
      // 表格翻页
      changePage (index) {
        // 这里直接更改了模拟的数据，真实使用场景应该从服务端获取数据
        this.pageIndex = index;
        this.getUserData();
      },
      // 表格每页数量切换
      pageSizeChange(pagesize){
        this.pageSize = pagesize;
        this.pageIndex = 1;
        this.getUserData();
      },
      //Model 处理
      handelSubmit(){

        this.$refs['editForm'].validate((valid) => {
          if (valid) {
//
            //编辑
            this.editForm.applicationIdList = [this.applicationId];
            let para = this.editForm;

            if(para.applicationId>0){
              //编辑
            }else{
              addUser(para).then((res) => {
                this.doHttp(res);
              });
            }
          } else {
            this.$Message.error('表单验证失败!');
          }
        })


      },
      handelCancel(){
        this.showModal = false;
      },
      doHttp(res){
        if(res.status==200){
          if(res.data.code==0){
            this.showModal = false;
            this.getUserData();
            this.editForm={
              userName:'',
              countryCode:'+86',
              email:'',
              gender:0,
              mobile:'',
              password:'',
              userType:0,
              applicationIdList:[this.applicationId]
            }
            this.$Message.success('提交成功!');
          }else{
            if(res.data.Message){
              this.$Message.error(res.data.Message);
            }
          }
        }else{
          this.$Message.error('接口操作失败!');
        }

      },

      // 绑定应用 复选框
      //全选
      handleCheckAll () {
        if (this.indeterminate) {
          this.checkAll = false;
        } else {
          this.checkAll = !this.checkAll;
        }
        this.indeterminate = false;

        if (this.checkAll) {

          this.bindForm.bindApplicationIds = this.applications.map(v => v.applicationId);

        } else {

          this.bindForm.bindApplicationIds = [];

        }
      },
      // 单选
      checkAllGroupChange(data){
        if (data.length === this.applications.length) {
          this.indeterminate = false;
          this.checkAll = true;
        } else if (data.length > 0) {
          this.indeterminate = true;
          this.checkAll = false;
        } else {
          this.indeterminate = false;
          this.checkAll = false;
        }

        this.bindForm.bindApplicationIds = data;
      },
      handelBindSubmit(){

        this.$refs['bindForm'].validate((valid) => {
          if (valid) {
//            //编辑

            let para = {
              bindUserId:this.bindForm.userId,
              applicationIdList : this.bindForm.bindApplicationIds
            };

            bindApplication(para).then((res) => {

              this.showBindForm = false;
              this.getUserData();
              this.$Message.success('绑定完成!');
            });

          } else {
            this.$Message.error('表单验证失败!');
          }
        })


      },
    },
    computed: {

    },
    mounted () {
      this.getApplicationData();
    }
  };
</script>
