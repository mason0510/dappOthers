<template>
	<div id="houseDetails">
		<Modal v-model="modal.value" title="详情" class-name="big-model">
			<img :src="details.imageUrl+details.listImage" alt="" class="imgsize">
			<p class="mt8"><span>日期 :</span>{{ new Date(details.updatedAt) }}</p>
			<p class="mt8"><span>地区 :</span>{{details.region}}</p>
			<p class="mt8"><span>城市 :</span>{{details.cityZH}}</p>
			<p class="mt8"><span>地址 :</span>{{details.address}}</p>
			<p class="mt8"><span>浴室数量 :</span>{{details.baths}}</p>
			<p class="mt8"><span>卧室数量 :</span>{{details.beds}}</p>
			<p class="mt8"><span>房屋类型 :</span>{{details.buildingType | houseType(details.buildingType)}}</p>
			<p class="mt8"><span>建造年份 :</span>{{details.builtIn }}</p>
			<p class="mt8"><span>所在区域 :</span>{{details.city }}</p>
			<p class="mt8"><span>委托截止期限 :</span>{{details.commissionDeadLine }}</p>
			<p class="mt8"><span>房屋面积 :</span>{{details.floorSize }}</p>
			<p class="mt8"><span>房屋介绍 :</span>{{details.houseIntro}}</p>
			<p class="mt8"><span>土地面积 :</span>{{details.landSize}}</p>
			<p class="mt8"><span>物业费 :</span>{{details.manageFee }}</p>
			<p class="mt8"><span>mls编号 :</span>{{details.mlsno }}</p>
			<p class="mt8"><span>照片 :</span>{{details.picList }}</p>
			<p class="mt8"><span>邮编 :</span>{{details.postCode }}</p>
			<p class="mt8"><span>地区编码 :</span>{{details.regionCode }}</p>
			<p class="mt8"><span>状态 :</span>{{details.status | houseStatus(details.status)}}</p>
			<p class="mt8"><span>配套设施 :</span>{{details.supportingList }}</p>
			<p class="mt8"><span>周边设施 :</span>{{details.surroundingList }}</p>
			<div slot="footer">
				<Button type="primary" @click="close">{{btnName}}</Button>
			</div>
		</Modal>
	</div>
</template>
<script>
	export default {
		props: {
			modal: {
				type: Object,
				default: function() {
					return {
						value: false
					}
				}
			},
			details: {
				type: Object,
				default: function() {
					return {}
				}
			},
			btnName: {
				type: String,
				default: '关闭'
			}
		},
		methods: {
			close() {
				this.$emit('close')
			}
		},
	}
</script>
<style scoped="scoped" lang="less">
	.mt8 {
		margin-top: 2px;
		span {
			text-align: right;
			vertical-align: middle;
			font-size: 12px;
			display: inline-block;
			color: #495060;
			line-height: 1;
			padding: 6px 12px 6px 0px;
			box-sizing: border-box;
			width: 100px;
		}
	}

	.imgsize {
		width: 100%;
	}
</style>
