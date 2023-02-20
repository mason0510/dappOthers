
<template>
  <div>
    <Row :gutter="10">
      <Card>
        <yztz-add-panel :searchTxt="searchTxt" :showSearch="false" placeholder="你好！" v-on:handleSearch="handleSearch" v-on:add="handleAdd"></yztz-add-panel>

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
          <FormItem label="名称" prop="name">
            <Input v-model="editForm.name"  />
          </FormItem>
          <FormItem label="别名" prop="nickName">
            <Input v-model="editForm.nickName"  />
          </FormItem>

          <FormItem label="绑定" >
            <Checkbox v-model="editForm.isAutoBind">注册自动绑定</Checkbox>
          </FormItem>
          <div style="display: none">
            <Input v-model="editForm.applicationId"  />
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
  import {getApplicationList,addApplication,updateApplication} from '../../api/application'

  export default {
    data () {
      return {
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
          name: [
            { required: true, message: '应用名称不能为空', trigger: 'blur' }
          ],
          nickName: [
            { required: true, message: '应用别名不能为空', trigger: 'blur' }
          ]
        },
        editForm:{
          name:'',
          nickName:'',
          isAutoBind:0,
          applicationId:0,
          status:0
        }

      };
    },
    methods: {
      // 初始化信息
      getData () {

        this.loading = true;


        getApplicationList().then((res) => {
          if(res.status==200){
            if(res.data.code==0){
              this.columns = tableColumns.applicationColumns;
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
      // Component Add-Panel
      handleSearch(keywords){
        this.searchTxt = keywords;
        alert(this.searchTxt);
      },
      handleAdd(){
        this.modalTitle = "新增-应用";
        this.showModal = true;
        this.editForm={
          name:'',
          nickName:'',
          isAutoBind:0,
          applicationId:0,
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
        this.editForm=Object.assign({}, row);
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
            if(para.applicationId>0){
              //编辑
              updateApplication(para).then((res) => {
                this.doHttp(res);
              });
            }else{
              addApplication(para).then((res) => {
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
            this.getData();
            this.editForm={
              name:'',
              nickName:'',
              isAutoBind:0,
              applicationId:0,
              status:0
            };
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
      this.getData();
    }
  };
</script>


