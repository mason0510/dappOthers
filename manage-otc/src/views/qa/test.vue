

<template>
    <div>
        <Row :gutter="10">
            <Card>
              <yztz-add-panel :searchTxt="searchTxt" placeholder="你好！" v-on:handleSearch="handleSearch" v-on:add="handleAdd"></yztz-add-panel>
              <!--<yztz-table :columns="columns" v-model="tableData" @selectionChange=selectionChange></yztz-table>-->

              <yztz-table :columns="columns"
                          :tableData="tableData"
                          :loading="loading"
                          :total="total"
                          :pageSize="pageSize"
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
            :title="modalTitle"
            width="900">
            <Form :label-width="80" :model="editForm" ref="editForm" :rules="editFormValidate">
              <FormItem label="文章标题" prop="articleTitle">
                <Input v-model="editForm.articleTitle"  />
              </FormItem>
              <FormItem label="文章类型" >
                <Select  style="width:90px" value="草稿">
                  <Option v-for="item in editForm.articleStateList" :value="item.value" :key="item.value">{{ item.value }}</Option>
                </Select>
              </FormItem>
              <FormItem label="文章内容" prop="articleContent">
                <div class="margin-top-20">
                  <textarea id="articleEditor" v-model="editForm.articleContent"></textarea>
                </div>
              </FormItem>
            </Form>
            <div slot="footer">
              <Button   @click="handelCancel">取消</Button>
              <Button type="primary"  @click="handelSubmit">提交</Button>
            </div>
          </Modal>

        </Row>
    </div>
</template>

<script>

  import tableData from '../tables/components/table_data.js';
  import tinymce from 'tinymce';
    export default {
        data () {
            return {
              //搜索
              searchTxt : '',
              //table
              columns: [],
              loading: false,
              tableData: [],
              pageSize:20,
              total:35,
              //编辑
              showModal:false,
              modelTitle:'',
              //表单内容
              editFormValidate: {
                articleTitle: [
                  { required: true, message: '标题不能为空', trigger: 'blur' }
                ]
              },
              editForm:{
                articleTitle:'',
                articleStateList: [{value: '草稿'}, {value: '等待复审'}],
                articleContent:''
              }

            };
        },
        methods: {
          // 初始化信息
          getData () {
            this.loading = true;
            this.tableData = tableData.table1Data;
            this.columns = tableData.table1Columns;
            this.loading = false;
          },
          // Component Add-Panel
          handleSearch(keywords){
            this.searchTxt = keywords;
            alert(this.searchTxt);
          },
          handleAdd(){
            this.modalTitle = "新增";
            this.showModal = true;
          },
          // 表格内容
          //表格行 选中后变化
          selectionChange(selection){
            alert(JSON.stringify(selection));
          },
          // 编辑表格
          editRow(row,index){

            alert(index);

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
                this.showModal = false;
                this.$Message.success('提交成功!');
              } else {
                this.$Message.error('表单验证失败!');
              }
            })


          },
          handelCancel(){
            this.showModal = false;
          }
        },
        computed: {

        },
        mounted () {
          this.getData();
          tinymce.init({
            selector: '#articleEditor',
            branding: false,
            elementpath: false,
            height: 300,
            language: 'zh_CN.GB2312',
            menubar: 'edit insert view format table tools',
            theme: 'modern',
            plugins: [
              'advlist autolink lists link image charmap print preview hr anchor pagebreak imagetools',
              'searchreplace visualblocks visualchars code fullscreen fullpage',
              'insertdatetime media nonbreaking save table contextmenu directionality',
              'emoticons paste textcolor colorpicker textpattern imagetools codesample'
            ],
            toolbar1: ' newnote print fullscreen preview | undo redo | insert | styleselect | forecolor backcolor bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image emoticons media codesample',
            autosave_interval: '20s',
            image_advtab: true,
            table_default_styles: {
              width: '100%',
              borderCollapse: 'collapse'
            }
          });
        },
      destroyed () {
        tinymce.get('articleEditor').destroy();
      }
    };
</script>


