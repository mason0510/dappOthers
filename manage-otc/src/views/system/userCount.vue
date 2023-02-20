<template>
	<div>
		<Row :gutter="10">
			<Card>
				<p slot="title" style="height: 40px;line-height: 35px">
					<span>
             <DatePicker type="datetimerange" v-model="timeSection" format="yyyy-MM-dd" placeholder="请选择时间区间" style="width: 200px"></DatePicker>
          </span>
					<span><Button  style="width:90px;"  icon="ios-search" type="ghost" @click="handleSearch">搜索</Button></span>
				</p>
				<yztz-table :columns="columns" :tableData="tableData" :loading="loading" :total="total" :pageSize="pageSize" :showOP="false" @changePage="changePage"  @doOther='doOther' @selectionChange=selectionChange @pageSizeChange="pageSizeChange">

				</yztz-table>
			</Card>
		</Row>
	</div>
</template>

<script>
	//import tableColumns from './columns.js';
	// import tinymce from 'tinymce';
	import Row from "../../../node_modules/iview/src/components/grid/row.vue";
	import { userCount} from '../../api/user'
	var moment = require('moment')
	export default {
		components: {
			'Row': Row
		},
		data() {
			return {
				timeSection:[],
				//table
				columns: [],
				loading: false,
				tableData: [],
				pageSize: 20,
				pageIndex: 1,
				total: 0,
			}
		},
		methods: {
			// 初始化信息
			getData() {
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
		        	para.endTime=endTime.substring(0,10)+' 23:59:59'
		        }else{
		        	para.startTime=''
		        	para.endTime=''
		        }
		        console.log('endTime='+para.endTime)
				userCount(para).then((res) => {
					if(res.status == 200) {
						if(res.data.success == true) {
							if(res.data.data) {
								this.columns = [
									{
										title: '',
										type: 'index',
										width: 60,
										align: 'center'
									},
									{
										title: '渠道',
										align: 'center',
										key:'userFrom'
									},
									{
										title: '统计数',
										align: 'center',
										key:'count'
									},
									{
										title: '总统计数',
										align: 'center',
										key:'countTotal'
									},
								];
								this.tableData = res.data.data;
							} else {
								this.tableData = [];
							}

							this.total = res.data.totalResults;
						} else {
							if(res.data.resultMsg) {
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
			// 删除表格
			deleteRow(row, index) {
				//description
				let para = {
					currencyId: row.currencyId,
				};
				deleteExchange(para).then((res) => {
					if(res.status == 200) {
						if(res.data.success == true) {
							if(res.data.data) {
								this.tableData.splice(index, 1);
								this.$Message.success('删除成功!');

							} else {
								this.$Message.error(res.data.resultMsg);
							}

						} else {
							if(res.data.resultMsg) {
								this.$Message.error(res.data.resultMsg);
							}
						}
					} else {
						this.$Message.error('删除失败!');
					}
				});

				//alert(JSON.stringify(row));
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
			doOther(currentRow, index, e) {
				let _this = this;
				if(e.type === 3) {
					this.houseId = currentRow.id;
				}
			},
			close() {
				this.modal.value = false;
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