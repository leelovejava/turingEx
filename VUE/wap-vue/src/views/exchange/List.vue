<template>
	<div class="exchange-list pb-20">
		<fx-account></fx-account>
		<div class="mt-2">
			<div class="tabs flex">
				<div class="tab-item" v-for="(item, index) in tabList" @click="selectIndex(index)"
					:class="[tabIndex === index ? 'active' : '']" :key="index">
					{{ item.title }}
					<!-- ({{ item.value }}) -->
				</div>
			</div>
			<van-pull-refresh v-model="refreshing" @refresh="onRefresh">
				<div class="all-wrap" v-if="tabIndex === 1">{{ $t('executable') }}({{ implementNumber }})</div>
				<div v-if="tabIndex === 0" class="p-4" v-for="item in list" :key="item">
					<fx-pair :item="item.info"></fx-pair>
					<van-cell :title="_item.title" @click="openShow(item.info)" v-for="(_item, _index) in item.listData"
						:key="_index">
						<template #value>
							<span
								:class="[_index === 0 && _item.value == $t('buy') ? 'blue' : '', _index === 0 && _item.value == $t('sell') ? 'red' : '', _index === 5 && _item.value > 0 ? 'green' : '', _index === 5 && _item.value < 0 ? 'red' : '']">{{
									_item.value
								}}</span>
						</template>
					</van-cell>
				</div>
				<div v-else class="p-4">
					<div v-for="item in list" :key="item">
						<fx-pair :item="item.info"></fx-pair>
						<van-cell :title="_item.title" @click="openShow(item.info)" :value="_item.value"
							v-for="(_item, _index) in item.listData" :key="_index">
							<template #value>
								<span
									:class="[_index === 0 && _item.value == $t('buy') ? 'blue' : '', _index === 0 && _item.value == $t('sell') ? 'red' : '']">{{
										_item.value
									}}</span>
							</template>
						</van-cell>
					</div>
				</div>
			</van-pull-refresh>
		</div>
		<van-popup v-model:show="isShow" closeable round position="bottom"
			:style="{ height: tabIndex === 0 ? '30%' : '45%' }">
			<div class="popup-wrap">
				<div class="sell-warap flex">
					<div class="pair-left">
						<fx-pair :item="checkInfo"></fx-pair>
						<div class="order-no">{{ $t('orderNumber') }}：<span>{{ checkInfo.item.order_no }}</span></div>
					</div>
					<div class="pair-right">
						<div class="title">{{ getStatus(tabIndex == 0 ? checkInfo.item.state :
							checkInfo.item.order_price_type) }}</div>
						<div class="text" :class="[checkInfo.item.direction == 'buy' ? 'blue' : 'red']">{{
							checkInfo.item.direction == 'buy' ? $t('buy') : $t('sell')
						}} {{ year }}@{{ formatNumber(checkInfo.item.mark_price) }}</div>
					</div>
				</div>
				<van-cell class="cell-item-h" v-if="tabIndex === 1 && checkInfo.item.state == 'submitted'">
					<template #title>
						<div class="cell-warp flex" @click="cancelOrder">
							<img src="@/assets/image/trade/the-icon4.png" />
							<span class="custom-title">{{ $t('cancelOrder') }}</span>
						</div>
					</template>
				</van-cell>
				<!-- <van-cell class="cell-item-h" v-if="tabIndex === 1">
					<template #title>
						<div class="cell-warp flex">
							<img src="@/assets/image/trade/the-icon5.png" />
							<span class="custom-title">修改订单</span>
						</div>
					</template>
				</van-cell> -->
				<van-cell class="cell-item-h" v-if="tabIndex === 1">
					<template #title>
						<div class="cell-warp flex" @click="onItem(1)">
							<img src="@/assets/image/trade/the-icon3.png" />
							<span class="custom-title">{{ $t('newOrder') }}</span>
						</div>
					</template>
				</van-cell>
				<van-cell class="cell-item-h" v-if="tabIndex === 0">
					<template #title>
						<div class="cell-warp flex" @click="openWarehouse">
							<img src="@/assets/image/trade/the-icon2.png" />
							<span class="custom-title">{{ $t('closePosition') }}</span>
						</div>
					</template>
				</van-cell>
				<van-cell class="cell-item-h" :title="$t('open') + checkInfo.symbol + $t('chart')">
					<template #title>
						<div class="cell-warp flex" @click="onItem(2)">
							<img src="@/assets/image/trade/the-icon1.png" />
							<span class="custom-title">{{ $t('open') }} {{ checkInfo.symbol }} {{ $t('chart') }}</span>
						</div>
					</template>
				</van-cell>
			</div>
		</van-popup>
	</div>
</template>

<script setup>
import fxPair from '@/components/fx-pair/index.vue'
import fxAccount from '@/components/fx-account/index.vue'
import { useUserStore } from '@/store/user';
import { useI18n } from 'vue-i18n'
import {
	Popup, showToast
} from 'vant';
import {
	ref,
	onMounted, onBeforeMount, onBeforeUnmount
} from 'vue';
import {
	useRouter,
	useRoute
} from 'vue-router';
import {
	contractOrder,
	_contractApplyOrderCancel,
	_contractApplyOrderList
} from "@/service/trade.api.js";
import { formatNumber } from '@/utils'


const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const year = ref('')

const openWarehouse = () => {
	router.push({ path: '/exchange/warehouse', query: { checkInfo: JSON.stringify(checkInfo.value) } })
}

const tabList = ref([{
	title: t('position'),
	value: ''
},
{
	title: t('entrustOrder'),
	value: ''
},
])
const checkInfo = ref({})
const list = ref([])
const implementNumber = ref(0)
let orderType = ref('orders')
let isShow = ref(false)
let tabIndex = ref(0)
const loading = ref(false);
const refreshing = ref(false)
const page = ref(1)
const noData = ref(false)
const finished = ref(false)
const interval = ref(null)

onMounted(async () => {
	let date = new Date()
	year.value = date.getFullYear()
	getContractOrder()
	interval.value = setInterval(() => {
		getContractOrder()
	}, 1000)
})

onBeforeMount(() => {
	if (userStore.userInfo || !userStore.userInfo.token) {
		if (!userStore.userInfo.token) {
			router.push('/login')
		}
	}
})

onBeforeUnmount(() => {
	if (interval.value) {
		clearInterval(interval.value)
	}
})

const getStatus = (index) => {
	if (index == 'created') {
		return t('ClosedPosition')
	} else if (index == 'limit') {
		return t('limitOrder')
	} else {
		return t('position')
	}
}

const onRefresh = () => {
	finished.value = false
	loading.value = true
	list.value = []
	page.value = 1
	noData.value = false
	getContractOrder()
}

const selectIndex = (index) => {
	tabIndex.value = index
	// if (index == 0) {
	// orderType.value = 'hisorders'
	// } else {
	orderType.value = 'orders'
	// }
	page.value = 1
	list.value = []
	getContractOrder()
}

const openShow = (item) => {
	isShow.value = true
	checkInfo.value = item
}

const onItem = (id) => {
	if (id === 1) { // 详情
		router.push('/order/' + checkInfo.value.symbol)
	} else if (id === 2) { //图表
		router.push(`/chart/index?symbol=${checkInfo.value.symbol}`)
	}

}

const cancelOrder = () => {
	let obj = {
		order_no: checkInfo.value.item.order_no
	}
	isShow.value = false
	orderType.value = 'orders'
	page.value = 1
	list.value = []
	implementNumber.value = 0
	_contractApplyOrderCancel(obj).then((res) => {
		showToast(t('submitSuccess'))
		getContractOrder()
	})
}

const getContractOrder = () => {
	let obj = {
		type: orderType.value,
		page_no: page.value,
		page_size: 'all'
	}
	let api = tabIndex.value == 0 ? contractOrder : _contractApplyOrderList
	api(obj).then((res) => {
		loading.value = false;
		refreshing.value = false;
		if (tabIndex.value == 0) { //持仓
			let array = []
			res.map((item) => {
				let obj = {
					listData: [
						{
							title: t('buySell'),
							value: item.direction == 'buy' ? t('buy') : t('sell')
						},
						{
							title: t('number'),
							value: `${item.volume_open / (item.lever_rate ? item.lever_rate : 1)}*${item.lever_rate ? item.lever_rate : 1}x`
						},
						{
							title: t('newPrice'),
							value: formatNumber(item.mark_price)
						},
						{
							title: t('AverageTransactionPrice'),
							value: formatNumber(item.trade_avg_price)
						},
						{
							title: t('openingTime'),
							value: item.create_time
						},
						{
							title: t('profit'),
							value: item.profit == 0 ? '--' : item.profit
						},
						{
							title: t('orderNumber'),
							value: item.order_no
						}
					],
					info: {
						name: item.name,
						symbol: item.symbol,
						status: item.state,
						item: item

					}
				}
				array.push(obj)
			})
			list.value = array
		} else {
			let array = []
			res.map((item) => {
				let obj = {
					listData: [
						{
							title: t('buySell'),
							value: item.direction == 'buy' ? t('buy') : t('sell')
						},
						{
							title: t('number'),
							value: `${item.volume_open / (item.lever_rate ? item.lever_rate : 1)}*${item.lever_rate ? item.lever_rate : 1}x`
						},
						{
							title: t('price'),
							value: item.price
						},
						{
							title: t('state'),
							value: getStatus(item.order_price_type)
						},
						{
							title: t('openingTime'),
							value: item.create_time
						},
						{
							title: t('orderNumber'),
							value: item.order_no
						}
					],
					info: {
						name: item.name,
						symbol: item.symbol,
						status: item.state,
						item: item

					}
				}
				array.push(obj)
			})
			list.value = array
		}
		if (tabIndex.value == 1) {
			implementNumber.value = res.length
			// list.value.map((item) => {
			// 	if (item.info.status == 'submitted') {
			// 		implementNumber.value = implementNumber.value + 1
			// 	}
			// })
		}
		finished.value = true
		// 如果没有数据，显示暂无数据
		if (list.value.length === 0 && page.value === 1) {
			noData.value = true
		}
		// // 如果加载完毕，显示没有更多了
		// if (res.length == 0) {
		// 	finished.value = true
		// }
	})
}
</script>
<style lang="scss" scoped>
:deep(.van-popup__close-icon) {
	width: 26px;
	height: 26px;
	display: flex;
	align-items: center;
	justify-content: center;
	background: #E6E6E9;
	border-radius: 50%;
	font-size: 14px;
	color: #6D6E74;
}

.exchange-list-box {
	:deep(.van-cell__title) {
		width: 120px;
		flex: none;
		color: var(--van-cell-value-color);
	}

	:deep(.van-cell__value) {
		flex: 1;
		color: var(--van-cell-text-color);
	}
}


.popup-wrap {
	// padding: ;
	padding: 50px 0 0 0;

	.pair-left {
		flex: 1;

		.order-no {
			padding-left: 80px;
			color: $text_color5;
			font-size: 12px;

			span {
				color: $black;
			}
		}
	}

	.pair-right {
		padding-right: 15px;

		.title {
			color: $text_color5;
			font-size: 12px;
			padding-top: 27px;
			text-align: right;
		}

		.text {
			font-size: 12px;
			padding-top: 2px;
		}
	}

	.custom-title {
		font-size: 16px;
		color: #1F2025;
	}

	.cell-warp {
		align-items: center;

		img {
			display: block;
			width: 17px;
			height: 21px;
			margin-right: 10px;
		}
	}
}

.tabs {
	padding: 0 15px;
	border-bottom: 1px solid #eeeeee;
	margin-top: 30px;

	.tab-item {
		width: 100px;
		text-align: center;
		padding: 10px 0;
		font-size: 16px;
		color: $text_color5;
	}

	.active {
		color: #1F2025 !important;
		border-bottom: 2px solid $btn_main;
	}
}

.cell-item-h {
	background: $red;
	padding: 15px 20px;
}

.all-wrap {
	width: 100px;
	background: $btn_main;
	margin: 15px;
	height: 26px;
	line-height: 26px;
	color: $text_color;
	font-size: 14px;
	border-radius: 20px;
	text-align: center;
}

.blue {
	color: $active_line !important;
}

.red {
	color: $red !important;
}


.green {
	color: $green !important;
}

:deep(.van-cell:after) {
	border-bottom: none !important
}

:deep(.van-cell) {
	background: $mainbgWhiteColor !important;
}
</style>
