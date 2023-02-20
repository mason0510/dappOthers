<template>
	<div>
		<Row :gutter="10">
			<Card>
				<p slot="title" style="height: 40px;line-height: 35px">
          <span>
            <Select  v-model="status" placeholder="认证状态" style="width: 120px">
              <Option v-for="item in statusList" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>
          </span>
					<span><Button  style="width:90px;"  icon="ios-search" type="ghost" @click="handleSearch">搜索</Button></span>
				</p>
        <yztz-table :columns="columns" :tableData="tableData" :loading="loading" :total="total" :pageSize="pageSize"
                    :showOP="false" @changePage="changePage" @editRow="editRow" @viewRow="viewRow"
                     @selectionChange=selectionChange @pageSizeChange="pageSizeChange">
        </yztz-table>
			</Card>
      <Modal v-model="viewModal" :title="modalTitle" :mask-closable="false" class-name="big-model">
        <Form :label-width="80" :model="editForm" ref="editForm">
          <FormItem label="真实姓名">
            {{editForm.realName }}
          </FormItem>
          <FormItem label="证件类型">
            {{editForm.documentType | documentType}}
          </FormItem>
          <FormItem label="证件号码">
            {{editForm.identityNumber}}
          </FormItem>
          <FormItem label="证件照正面">
            <div class="admin-upload-list" v-if="editForm.imageAddress1!=''">
              <template>
                <img :src="getImage(editForm.imageAddress1)">
                <div class="admin-upload-list-cover">
                  <Icon type="ios-eye-outline" @click.native="handleView(editForm.imageAddress1)"></Icon>
                </div>
              </template>
            </div>
          </FormItem>
          <FormItem label="证件照背面" prop="imageAddress2">
            <div class="admin-upload-list" v-if="editForm.imageAddress2!=''">
              <template>
                <img :src="getImage(editForm.imageAddress2)">
                <div class="admin-upload-list-cover">
                  <Icon type="ios-eye-outline" @click.native="handleView(editForm.imageAddress2)"></Icon>
                </div>
              </template>
            </div>
          </FormItem>
          <FormItem label="手持证件照">
            <div class="admin-upload-list" v-if="editForm.imageAddress3!=''">
              <template>
                <img :src="getImage(editForm.imageAddress3)">
                <div class="admin-upload-list-cover">
                  <Icon type="ios-eye-outline" @click.native="handleView(editForm.imageAddress3)"></Icon>
                </div>
              </template>
            </div>
          </FormItem>
          <FormItem label="认证状态">
            {{editForm.status | statusType}}
          </FormItem>
          <FormItem label="驳回理由">
            <template>
              <CheckboxGroup v-model="editForm.rejectReasonList">
                <Checkbox label="INCOMPLETE"><span>边角不完整</span></Checkbox>
                <Checkbox label="FONT_ERROR"><span>字体不清晰</span></Checkbox>
                <Checkbox label="LIGHT_ERROR"><span>亮度不均匀</span></Checkbox>
              </CheckboxGroup>
            </template>
          </FormItem>
          <FormItem label="其他理由">
            <Input v-model="editForm.otherReason" type="textarea" :rows="4" placeholder="其他驳回理由" />
          </FormItem>
        </Form>
        <div slot="footer">
          <Button @click="handelCancel">取消</Button>
          <Button type="primary" @click="doAuthentication(editForm.id,3, editForm._index)">提交</Button>
        </div>
      </Modal>
      <yztz-preview-image
        @closePreview="closePreview"
        :imageUrl="showPreviewImageUrl"
        :imageShow="showPreviewImage">
      </yztz-preview-image>
		</Row>
	</div>
</template>

<script>
	import Row from "../../../node_modules/iview/src/components/grid/row.vue";
  import {otcImageUrl} from '../../api/imageUrl'
  import {identityList, authentication} from '../../api/identity'
	export default {
		components: {
			'Row': Row
		},
		data() {
			return {
				//认证状态
				status :1,
				statusList: [{
					label: '全部',
					value: ''
				}, {
					label: '未认证',
					value: 0
				}, {
					label: '待认证',
					value: 1
				}, {
					label: '已认证',
					value: 2
				}, {
					label: '认证驳回 ',
					value: 3
				}],
				//详情
				viewModal:false,
        modalTitle:'',
				editForm:{},
				//table
				columns: [],
				loading: false,
				tableData: [],
				pageSize: 20,
				pageIndex: 1,
				total: 0,
        //图片查看
        showPreviewImage:false,
        showPreviewImageUrl:''
			}
		},
		methods: {
			// 初始化信息
			getData() {
				this.loading = true;
				let para = {
					status :this.status ,
          currentPage: this.pageIndex,
          pageSize: this.pageSize
				}
        identityList(para).then((res) => {
          if (res.status == 200) {
            if (res.data.success == true) {
              if (res.data.data) {
                this.columns = [
                  {
                    title: '',
                    type: 'index',
                    width: 60,
                    align: 'center'
                  },
                  {
                    title: '真实姓名',
                    align: 'center',
                    width: 100,
                    key: 'realName'
                  },
                  {
                    title: '证件类型',
                    width: 140,
                    align: 'center',
                    render: (h, params) => {
                      const row = params.row;
                      let type = ''
                      switch (row.documentType) {
                        case 0:
                          type = '身份证'
                          break
                        case 1:
                          type = '护照'
                          break
                        default:
                          break;
                      }
                      return h('p', type)
                    }
                  },
                  {
                    title: '证件号码',
                    align: 'center',
                    width: 200,
                    key: 'identityNumber'
                  },
                  {
                    title: '证件照正面',
                    width: 120,
                    align: 'center',
                    render: (h, params) => {
                      const row = params.row;
                      let photo = row.imageAddress1;
                      if (photo) {
                        let imgUrl = otcImageUrl + photo
                        return h('img', {
                          attrs: {
                            src: imgUrl + '?imageView2/1/w/35/h/35',
                          },
                          style: {
                            display: 'inline-block',
                            // border:'3px solid',
                            cursor: 'pointer',
                            marginTop: '5px',
                            height:'80px',
                            width:'80px'
                          },
                          on: {
                            click: () => {
                              this.handleView(row.imageAddress1)
                            }
                          }
                          //
                        })
                      } else {
                        return '';
                      }
                    }
                  },
                  {
                    title: '证件照反面',
                    width: 120,
                    align: 'center',
                    render: (h, params) => {
                      const row = params.row;
                      let photo = row.imageAddress2;
                      if (photo) {
                        let imgUrl = otcImageUrl + photo
                        return h('img', {
                          attrs: {
                            src: imgUrl + '?imageView2/1/w/35/h/35',
                          },
                          style: {
                            display: 'inline-block',
                            // border:'3px solid',
                            cursor: 'pointer',
                            marginTop: '5px',
                            height:'80px',
                            width:'80px'
                          },
                          on: {
                            click: () => {
                              this.handleView(row.imageAddress2)
                            }
                          }
                          //
                        })
                      } else {
                        return '';
                      }
                    }
                  },
                  {
                    title: '手持证件照',
                    width: 120,
                    align: 'center',
                    render: (h, params) => {
                      const row = params.row;
                      let photo = row.imageAddress3;
                      if (photo) {
                        let imgUrl = otcImageUrl + photo
                        return h('img', {
                          attrs: {
                            src: imgUrl + '?imageView2/1/w/35/h/35',
                          },
                          style: {
                            display: 'inline-block',
                            // border:'3px solid',
                            cursor: 'pointer',
                            marginTop: '5px',
                            height:'80px',
                            width:'80px'
                          },
                          on: {
                            click: () => {
                              this.handleView(row.imageAddress3)
                            }
                          }
                          //
                        })
                      } else {
                        return '';
                      }
                    }
                  },
                  {
                    title: '认证状态 ',
                    align: 'center',
                    width: 100,
                    render: (h, params) => {
                      const row = params.row;
                      let type = ''
                      switch (row.status) {
                        case 0:
                          type = '未认证'
                          break
                        case 1:
                          type = '待认证'
                          break
                        case 2:
                          type = '已认证'
                          break
                        case 3:
                          type = '认证驳回'
                          break
                        default:
                          break;
                      }
                      return h('p', type)
                    }
                  },
                  {
                    title: '操作',
                    align: 'center',

                    render: (h, params) => {
                      const row = params.row;
                      if (row.status == 1) {
                        return h('div', [
                          h('Poptip', {
                            props: {
                              confirm: true,
                              title: '您确定要审核通过吗?',
                              transfer: true
                            },
                            on: {
                              'on-ok': () => {
                                this.doAuthentication(row.id, 2, params.index);
                              }
                            }
                          }, [
                            h('Button', {
                              style: {
                                margin: '0 5px'
                              },
                              props: {
                                type: 'primary',
                                size: 'small',
                                placement: 'bottom'
                              }
                            }, '审核通过')
                          ]),
                          h('Button', {
                            props: {
                              type: 'error',
                              size: 'small'
                            },
                            style: {
                              marginRight: '5px'
                            },
                            on: {
                              click: () => {
                                this.reject(row, params.index)
                              }
                            }
                          }, '审核不通过')
                        ]);
                      }
                    }
                  }
                ];
                this.tableData = res.data.data;
              } else {
                this.tableData = [];
              }

              this.total = res.data.totalResults;
            } else {
              if (res.data.resultMsg) {
                this.$Message.error(res.data.resultMsg);
              }
            }
          } else {
            this.$Message.error('接口读取失败!');
          }
          this.loading = false;
        });
			},

			// Component Add-Panel
			handleSearch(keywords) {
				this.pageIndex=1;
				this.getData();
			},
			// 表格内容
			//表格行 选中后变化
			selectionChange(selection) {
				alert(JSON.stringify(selection));
			},
			// 驳回
			reject(row, index) {
				this.modalTitle = "驳回理由";
				this.$refs['editForm'].resetFields();
				this.viewModal = true;
        this.editForm=Object.assign({}, row);
			},
      //获取图片完整路径
      getImage(image){
			  return otcImageUrl + image;
      },
      //图片预览
      handleView(image){
        this.showPreviewImage = true;
        this.showPreviewImageUrl = otcImageUrl + image;
      },
      //关闭预览
      closePreview(){
        this.showPreviewImage = false;
        this.showPreviewImageUrl = '';
      },
			// 查看表格
			viewRow(row, index) {
				this.viewModal=true;
				this.editForm=Object.assign({}, row);
			},
      //编辑
      editRow(row, index){

      },
			// 表格翻页
			changePage(index) {
				// 这里直接更改了模拟的数据，真实使用场景应该从服务端获取数据
				this.pageIndex = index;
				this.getData();
			},
			// 表格每页数量切换
			pageSizeChange(pagesize) {
				this.pageIndex = 1;
				this.pageSize = pagesize;
				this.getData();
			},

      //审核处理
      doAuthentication(id,type,index){
        let para = {
          id: id,
          status: type,
          rejectReasons:this.editForm.rejectReasonList,
          otherReason:this.editForm.otherReason
        }
        authentication(para).then((res) => {
          if (res.status == 200) {
            if (res.data.success == true) {
              if (res.data.data) {
                if (this.status == 1) {
                  this.tableData.splice(index, 1);
                  if (type == 3) {
                    this.$refs['editForm'].resetFields();
                    this.viewModal = false;
                  }
                } else {
                  this.getData();
                }
                this.$Message.success('提交成功!');
              } else {
                this.$Message.error(res.data.resultMsg);
              }
            } else {
              if (res.data.resultMsg) {
                this.$Message.error(res.data.resultMsg);
              }
            }
          } else {
            this.$Message.error('接口读取失败!');
          }
        });
      },
			handelCancel() {
				this.viewModal = false;
			}
		},
		computed: {

		},
		mounted() {
			this.getData();
		},
	};
</script>
<style scoped="scoped" lang="less">
.mt8{
	span {
			text-align: right;
			vertical-align: middle;
			font-size: 12px;
			display: inline-block;
			color: #495060;
			line-height: 1;
			padding: 6px 12px 6px 0px;
			box-sizing: border-box;
			width: 180px;
		}
		.width180 {
			width: 180px;
		}
}
</style>
