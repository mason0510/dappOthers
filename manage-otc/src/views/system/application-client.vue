
<template>
  <div>
    <Row :gutter="10">
      <Card>
        <Row>
          <Col span="6">
            <Select v-model="applicationId" @on-change="applicationChanged" style="width:200px">
              <Option v-for="item in applications" :value="item.applicationId" :key="item.applicationId">{{ item.name }}</Option>
            </Select>
          </Col>
          <Col span="18">
            <yztz-add-panel :searchTxt="searchTxt" :showSearch="false" placeholder="你好！" v-on:handleSearch="handleSearch" v-on:add="handleAdd"></yztz-add-panel>
          </Col>
        </Row>


        <yztz-table :columns="columns"
                    :tableData="tableData"
                    :loading="loading"
                    :total="total"
                    :pageSize="pageSize"
                    viewText="客户端"
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
          <FormItem label="别名" prop="clientNickName">
            <Input v-model="editForm.clientNickName"  />
          </FormItem>

          <FormItem label="类型" >
            <RadioGroup v-model="editForm.clientType">
              <Radio label="2" >苹果
              </Radio>
              <Radio label="3" >安卓
              </Radio>
              <Radio label="1" >网页
              </Radio>
            </RadioGroup>
          </FormItem>
          <FormItem label="有效时长" prop="timeMillis">
            <Input v-model="editForm.timeMillis"  />
          </FormItem>
          <div style="display: none">
            <Input v-model="editForm.applicationClientId"  />
            <Input v-model="editForm.status"  />
          </div>
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

  import tableColumns from './columns.js';
  import {getApplicationList} from '../../api/application'
  import {getApplicationClientList,addApplicationClient,updateApplicationClient} from '../../api/applicationClient'

  export default {
    data () {
      return {
        //applications
        applications:[],
        applicationId:0,
        //
        spinShow:false,
        //搜索
        searchTxt : '',
        //table
        columns: [],
        loading: false,
        tableData: [],
        pageSize:20,
        total:0,
        //编辑
        showModal:false,
        modalTitle:'',
        //表单内容
        editFormValidate: {
          clientNickName: [
            { required: true, message: '客户端别名不能为空', trigger: 'blur' }
          ],
          timeMillis: [
            { required: true, message: '操作有效期不能为空', trigger: 'blur' }
          ]
        },
        editForm:{
          nickName:'',
          timeMillis:28800,
          clientType:0,
          applicationClientId:0,
          status:0
        }

      };
    },
    methods: {
      // 初始化信息
      getApplicationData () {

//        this.loading = true;


        getApplicationList().then((res) => {
          if(res.status==200){
            if(res.data.code==0){

              this.applications=res.data.data;

              if(this.applications.length>0){
                this.applicationId = this.applications[0].applicationId;
                //this.getClientData ();
              }


            }else{
              if(res.data.Message){
                this.$Message.error(res.data.Message);
              }
            }
          }else{
            this.$Message.error('接口读取失败!');
          }


//          this.loading = false;
        });

      },
      getClientData () {

        this.loading = true;



        getApplicationClientList(this.applicationId).then((res) => {
          if(res.status==200){
            if(res.data.code==0){
              this.columns = tableColumns.applicationClientColumns;
              this.tableData=res.data.data;
              this.total = res.data.data.length;
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

        this.getClientData();
      },

      // Component Add-Panel
      handleSearch(keywords){
        this.searchTxt = keywords;
        alert(this.searchTxt);
      },
      handleAdd(){
        this.modalTitle = "新增-应用";
        this.showModal = true;
        this.editForm={
          clientNickName:'',
          clientType:'1',
          secret:'',
          timeMillis:28800,
          applicationId:this.applicationId,
          applicationClientId:0,
          status:0
        }
      },
      // 表格内容
      //表格行 选中后变化
      selectionChange(selection){
        alert(JSON.stringify(selection));
      },
      // 编辑表格
      editRow(row,index){
        this.modalTitle = "新增-应用";
        this.showModal = true;
        this.editForm= this.tableData[index]; //Object.assign({}, row);
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
        alert(index);
      },
      // 表格每页数量切换
      pageSizeChange(pagesize){
        alert(pagesize);
      },
      //Model 处理
      handelSubmit(){

        this.$refs['editForm'].validate((valid) => {
          if (valid) {
//
            //编辑
            let para = this.editForm;
            if(para.applicationClientId>0){
              //编辑
              updateApplicationClient(para).then((res) => {
                this.doHttp(res);
              });
            }else{
              addApplicationClient(para).then((res) => {
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
            this.getClientData();
            this.editForm={
              clientNickName:'',
              clientType:'1',
              secret:'',
              timeMillis:28800,
              applicationId:this.applicationId,
              applicationClientId:0,
              status:0
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

      }
    },
    computed: {

    },
    mounted () {
      this.getApplicationData();

    }
  };
</script>


