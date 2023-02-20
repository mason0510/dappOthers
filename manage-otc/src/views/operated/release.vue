
<template>
  <div>
    <Row :gutter="10">
      <Card>
        <p slot="title" style="height: 40px;line-height: 35px">
          <span>
            <RadioGroup v-model="device">
              <Radio :label="0">
                  全部
              </Radio>
              <Radio :label="3">
                  安卓
              </Radio>
              <Radio :label="4">
                  苹果
              </Radio>
            </RadioGroup>
          </span>
          <span>
            <DatePicker type="daterange"  @on-change="handleChange" :value="value2" :options="options2" format="yyyy-MM-dd" placeholder="请选择起止时间" style="width: 250px"></DatePicker>
          </span>
          <span ><Input v-model="description"   placeholder="请输入描述" style="width: 160px;" /></span>

          <span ><Button  style="width:90px;"  icon="ios-search" type="ghost" @click="handleSearch">搜索</Button></span>
          <span ><Button  style="width:90px;" type="primary" @click="handleAdd">新增</Button></span>
        </p>

        <yztz-table :columns="columns"
                    :tableData="tableData"
                    :loading="loading"
                    :total="total"
                    :pageSize="pageSize"
                    :showOP = "false"
                    @changePage="changePage"
                    @editRow=editRow
                    @deleteRow="deleteRow"
                    @viewRow="viewRow"
                    @pageSizeChange="pageSizeChange">

        </yztz-table>


      </Card>

      <Modal
        v-model="showModal"
        :title="modalTitle" :mask-closable="false"
        width="600">
        <Form :label-width="80" :model="editForm" ref="editForm" :rules="editFormValidate">

                <FormItem label="设备" >
                  <RadioGroup v-model="editForm.device">
                    <Radio :label="3">
                        安卓
                    </Radio>
                    <Radio :label="4">
                        苹果
                    </Radio>
                  </RadioGroup>
              </FormItem>
          <FormItem label="构建版本" prop="buildVersion">
            <Input v-model="editForm.buildVersion"  />
          </FormItem>
          <FormItem label="升级类型" >
            <RadioGroup v-model="editForm.forceUpdate">
              <Radio :label="2">
                  非强制
              </Radio>
              <Radio :label="1">
                  强制
              </Radio>
            </RadioGroup>
        </FormItem>
        <FormItem label="发布时间">
          <DatePicker  :value="editForm.issueDate" format="yyyy-MM-dd HH:mm:ss" type="datetime" placeholder="请选择发布时间" style="width: 200px"></DatePicker>
        </FormItem>
        <FormItem label="下载地址" >
          <Input v-model="editForm.downloadUrl"  />
        </FormItem>
        <FormItem label="包大小" prop="pkgSize">
          <Input v-model="editForm.pkgSize"  />
        </FormItem>
        <FormItem label="显示版本" prop="releaseVersion">
          <Input v-model="editForm.releaseVersion"  />
        </FormItem>
        <FormItem label="描述" prop="description">
          <Input type="textarea" :rows="5" v-model="editForm.description"  />
        </FormItem>
        </Form>
        <div slot="footer">
          <Button   @click="handelCancel">取消</Button>
          <Button type="primary"  @click="handelSubmit">提交</Button>
        </div>
      </Modal>
      <Spin size="large" fix v-if="spinShow"></Spin>
    </Row>
  </div>
</template>

<script>

  import Row from "../../../node_modules/iview/src/components/grid/row.vue";
  import {getReleaseList,saveRelease,getReleaseById,getReleaseLatest,deleteRelease} from '../../api/release'
  export default {
    components: {
			Row
		},
    data () {
      const validateNumber = (rule, value, callback) => {
                if (!value) {
                    return callback(new Error('请选择资讯类型'));
                }
                // 模拟异步验证效果
                setTimeout(() => {
                    if (!Number.isInteger(value)) {
                        callback(new Error('请选择资讯类型'));
                    } else {
                        callback();
                    }
                }, 1000);
            };
      return {
        data:{},
        //
        spinShow:false,

        //搜索
        description : '',
        //table
        columns: [],
        loading: false,
        tableData: [],
        pageSize:20,
        total:35,
        pageIndex:1,
        //编辑
        showModal:false,
        modalTitle:'',
        //表单内容
        editFormValidate: {
          buildVersion: [
            { required: true, message: '构建版本不能为空', trigger: 'blur' }
          ]
        },

        device:0,
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

        editForm:{}

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
            afterDate = this.value2[0];
            beforeDate = this.value2[1];
          }
        }

        let para = {
          issueDateStart : afterDate,
          issueDateEnd : beforeDate,
          device : this.device == 0 ? '' : this.device,
          description : this.description,
          currentPage : this.pageIndex,
          pageSize : this.pageSize
        }


        getReleaseList(para).then((res) => {
          if(res.status==200){
            if(res.data.success==true){

              this.columns = [
                {
                  title: '序号',
                  type: 'index',
                  width: 70,
                  align: 'center'
                },
                {
                  title: '构建版本',
                  align: 'left',
                  key: 'buildVersion'
                },
                {
                  title: '设备',
                  width: 110,
                  align: 'center',
                  render: (h, params) => {
                      const row = params.row;
                      //类型


                      //地域
                      let text = '';
                      switch(row.device){
                        case 3:
                          text = '安卓';
                          break;
                        case 4:
                          text = '苹果';
                          break;
                        default :

                      }

                      return h('div', [h('p', text)]);

                  }
                },
                {
                  title: '描述',
                  align: 'left',
                  key: 'description'
                },
                {
                  title: '操作',
                  align: 'center',
                  width: 140,
                  key: 'handle',
                  handle: ['edit', 'delete']
                }
              ];

              if(res.data.data){
                this.tableData=res.data.data;
              }else{
                this.tableData=[];
              }

              this.total = res.data.totalResults;
            }else{
              if(res.data.resultMsg){
                this.$Message.error(res.data.resultMsg);
              }
            }
          }else{
            this.$Message.error('版本接口读取失败!');
          }
          this.loading = false;
        });


      },
      // Component Add-Panel
      handleSearch(){
      	this.pageIndex=1;
        this.getData();
      },

      handleAdd(){

        this.editForm = {
          buildVersion:'',
          description : '' ,
          device : 3 ,
          downloadUrl : '' ,
          forceUpdate : 2 ,
          issueDate : new Date() ,
          pkgSize : '' ,
          product : 1 ,
          releaseVersion : '' ,
          createdAt : new Date() ,
          id : null ,
          updatedAt : new Date()
        };

        this.modalTitle = "新增";
        this.showModal = true;
      },
      // 表格内容
      //表格行 选中后变化
      selectionChange(selection){

      },
      // 编辑表格
      editRow(row,index){
          this.modalTitle = "编辑-版本";
          this.showModal = true;
          this.editForm= Object.assign({}, row);
          this.editForm.buildVersion = this.editForm.buildVersion + '';

      },
      // 删除表格
      deleteRow(row,index){

        let para = {
          id:row.id,
        };
        deleteRelease(para).then((res) => {
          if(res.status==200){
            if(res.data.success==true){
              if(res.data.data){

                this.tableData.splice(index, 1);
                this.$Message.success('删除成功!');

              }else{
                this.$Message.error(res.data.resultMsg);
              }


            }else{
              if(res.data.resultMsg){
                this.$Message.error(res.data.resultMsg);
              }
            }
          }else{
            this.$Message.error('删除失败!');
          }
        });

      },
      // 查看表格
      viewRow(row,index){



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

        this.$refs['editForm'].validate((valid) => {
          if (valid) {
            this.loading = true;
            // 开始保存
            let para = this.editForm
            saveRelease(para).then((res) => {
              if(res.status==200){
                if(res.data.success==true){
                  // 成功
                  this.getData();
                  this.showModal = false;
                  this.$Message.success('提交成功!');
                }else{
                  if(res.data.resultMsg){
                    this.$Message.error(res.data.resultMsg);
                  }
                }
              }else{
                this.$Message.error('接口读取失败!');
              }
              this.loading = false;
            });

          } else {
            this.$Message.error('表单验证失败!');
          }
        })


      },
      handelCancel(){
        this.showModal = false;
      },
      //日期
      handleChange (arrDate) {
        this.value2 = arrDate;
      }
    },
    computed: {

    },
    mounted () {

      this.getData();

    },
    destroyed () {

    }
  };
</script>
