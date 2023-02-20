<template>
  <div>
    <Row :gutter="10">
      <Card>
        <p slot="title" style="height: 40px;line-height: 35px">
          <span>
            <Select v-model="position"  placeholder="选择位置" style="width: 100px">
              <Option v-for="item in positions" :value="item.code" :key="item.code">{{ item.name }}</Option>
            </Select>
          </span>
          <span>
            <Select v-model="urlType"  placeholder="链接类型" style="width: 100px">
              <Option v-for="item in urlTypes" :value="item.code" :key="item.code">{{ item.name }}</Option>
            </Select>
          </span>
          <span ><Button  style="width:90px;"  icon="ios-search" type="ghost" @click="handleSearch">搜索</Button></span>
          <span ><Button  style="width:90px;" type="primary" @click="handleAdd">新增</Button></span>
        </p>
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
        :title="modalTitle" :mask-closable="false"
        width="900">
        <Form :label-width="80" :model="editForm" ref="editForm" :rules="editFormValidate">
          <FormItem label="位置"  prop="position" >
            <Select  style="width:200px" v-model="editForm.position">
              <Option v-for="item in positions" :value="item.code" :key="item.code">{{ item.name }}</Option>
            </Select>
          </FormItem>
          <FormItem label="链接类型" prop="urlType" >
            <Select  style="width:200px" v-model="editForm.urlType">
              <Option v-for="item in urlTypes" :value="item.code" :key="item.code">{{ item.name }}</Option>
            </Select>
          </FormItem>
          <FormItem label="链接" >
            <Input v-model="editForm.url"  />
          </FormItem>
          <FormItem label="顺序" >
            <Input v-model="editForm.order"  />
          </FormItem>
          <FormItem label="描述" >
            <Input v-model="editForm.description"  />
          </FormItem>
          <FormItem label="图片">
            <div class="admin-upload-list" v-if="editForm.image!=''" >
                                                  <template >
                                                      <img :src="showImageUrl">
                                                      <div class="admin-upload-list-cover">
                                                          <Icon type="ios-eye-outline" @click.native="handleView(editForm)"></Icon>
                                                          <Icon type="ios-trash-outline" @click.native="handleRemove(editForm)"></Icon>
                                                      </div>
                                                  </template>

                                              </div>
          </FormItem>
          <FormItem label="上传">
            <yztz-upload-image :uploadUrl="uploadUrl"
            :uploadType="3" cropimg="cropimg23" preview ="preview23"
            @onUploaded="onUploaded" ></yztz-upload-image>
          </FormItem>
        </Form>
        <div slot="footer">
          <Button   @click="handelCancel">取消</Button>
          <Button type="primary"  @click="handelSubmit">提交</Button>
        </div>
      </Modal>

      <yztz-preview-image
        @closePreview="closePreview"
        :imageUrl="showPreviewImageUrl"
        :imageShow="showPreviewImage">
      </yztz-preview-image>
      <Spin size="large" fix v-if="spinShow"></Spin>
    </Row>
  </div>
</template>

<script>
  import {getBannerList,getBannerById,saveBanner,deleteBanner} from '../../api/banner'
  import {infoImageUrl} from '../../api/imageUrl'
  import {fileUploadUrl} from '../../api/system'
  import tableColumns from './columns.js';

  import Row from "../../../node_modules/iview/src/components/grid/row.vue";

  export default {
    components: {Row},
    data () {
      const validateNumber = (rule, value, callback) => {
                if (!value) {
                    return callback(new Error('请选择位置信息'));
                }
                // 模拟异步验证效果
                setTimeout(() => {
                    if (!Number.isInteger(value)) {
                        callback(new Error('请选择位置信息'));
                    } else {
                        callback();
                    }
                }, 1000);
            };
      const validateUrlType = (rule, value, callback) => {
            if (!value) {
              return callback(new Error('请选择链接信息'));
            }
                      // 模拟异步验证效果
                      setTimeout(() => {
                          if (!Number.isInteger(value)) {
                              callback(new Error('请选择链接信息'));
                          } else {
                              callback();
                          }
                      }, 1000);
                  };
      return {
        //图片查看
        showPreviewImage:false,
        showPreviewImageUrl:'',
        //
        showImageUrl:'',
        data:{},
        //
        spinShow:false,

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
        //表单内容
        editFormValidate: {
          position: [
            {  validator: validateNumber, trigger: 'change' }
          ],
          urlType: [
            {  validator: validateUrlType, trigger: 'change' }
          ],
        },

        country:'',
        positions:[{code: 0,name:'全部'},{code: 1,name:'发现'}],
        position: '',
        urlTypes:[{code: 0,name:'全部'},{code: 1,name:'网页'}],
        urlType: '',

        editForm:{
        },
        uploadUrl:fileUploadUrl

      };
    },
    methods: {
      // 初始化信息
      getData () {

        this.loading = true;

        let para = {
          country : this.country,
          position : this.position > 0 ? this.position : '',
          urlType : this.urlType > 0 ? this.urlType : '',
          currentPage : this.pageIndex,
          pageSize : this.pageSize
        }

        getBannerList(para).then((res) => {
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
                  title: '图片',
                  width: 70,
                  align: 'center',
                  render: (h, params) => {
                    const row = params.row;
                    let photo = row.image;
                    if (photo!=''){
                      let imgUrl = infoImageUrl + photo
                      return h('img', {
                       attrs: {
                          src: imgUrl  + '?imageView2/1/w/35/h/35',
                        },
                        style: {
                          display:'inline-block',
                          // border:'3px solid',
                          cursor:'pointer',
                          marginTop: '5px'
                        },
                        on: {
                            click: () => {
                                this.handleView(row)
                            }
                        }
                        //
                      })
                    }else{
                      return '';
                    }
                  }
                },
                {
                  title: '位置',
                  width: 90,
                  align: 'center',
                  render: (h, params) => {
                    const row = params.row;
                    let txt = '';
                    for(var x of this.positions){
                      if(row.position==x.code){
                        txt = x.name;
                        break;
                      }
                    }
                    return h('p', txt)
                  }
                },
                {
                  title: '顺序',
                  align: 'center',
                  width: 70,
                  key:'order'
                },
                {
                  title: '链接',
                  align: 'center',
                  render: (h, params) => {
                    const row = params.row;
                    let url = row.url;
                    if (url!=''){

                      return h('div', [h('a',{
                       attrs: {
                          href: url,
                          target:'_blank'
                        }
                      }, url)]);

                    }else{
                      return '';
                    }
                  }
                },
                {
                  title: '链接类型',
                  width: 90,
                  align: 'center',
                  render: (h, params) => {
                      const row = params.row;
                      //类型
                      let txtType = '';
                      for(var x of this.urlTypes){
                        if(row.urlType==x.code){
                          txtType = x.name;
                          break;
                        }
                      }
                      return h('p', txtType)
                  }
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
            this.$Message.error('接口读取失败!');
          }
          this.loading = false;
        });


      },
      // Component Add-Panel
      // Component Add-Panel
      handleSearch(){
      	this.pageIndex=1;
        this.getData();
      },

      handleAdd(){

        this.editForm = {
          country: this.country,
          createdAt:new Date() ,
          description: '' ,
          id :'',
          image : '' ,
          order : 0 ,
          position : this.position ,
          updatedAt : new Date() ,
          url : '' ,
          urlType : this.urlType
        };
this.$refs['editForm'].resetFields();
        this.modalTitle = "新增";
        this.showModal = true;
      },
      // 表格内容
      //表格行 选中后变化
      selectionChange(selection){

      },
      // 编辑表格
      editRow(row,index){
      	this.$refs['editForm'].resetFields();
          this.modalTitle = "编辑-Banner";
          this.showModal = true;
          this.editForm= Object.assign({}, row);
          this.showImageUrl = infoImageUrl + this.editForm.image;
      },
      // 删除表格
      deleteRow(row,index){

        let para = {
          id:row.id,
        };
        deleteBanner(para).then((res) => {
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

            saveBanner(para).then((res) => {
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
                this.$Message.error('Banner接口读取失败!');
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
      onUploaded(fileName){
        this.editForm.image = fileName;
        this.showImageUrl = infoImageUrl + this.editForm.image;
      },
      //删除预览
      handleRemove(item){
        this.editForm.image = '';
      },
      //预览
      handleView(item){
        this.showPreviewImage = true;
        this.showPreviewImageUrl = infoImageUrl + item.image;


      },
      //关闭预览
      closePreview(){
        this.showPreviewImage = false;
        this.showPreviewImageUrl = '';
      },
      //国家变化
      countryChanged(e){

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
