<template>
  <div class="delivery">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>配送车辆</span>
          </template>
          
          <el-table :data="vehicles" border>
            <el-table-column prop="plateNumber" label="车牌号" />
            <el-table-column prop="currentRegion" label="区域" />
            <el-table-column label="容量">
              <template #default="{ row }">
                {{ row.currentCapacity }} / {{ row.maxCapacity }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="row.status === 'IDLE' ? 'success' : 'warning'">
                  {{ row.status === 'IDLE' ? '空闲' : '配送中' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button v-if="row.status === 'IDLE'" size="small" type="primary" @click="createDelivery(row.id)">
                  创建配送
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>配送单</span>
          </template>
          
          <el-table :data="deliveries" border>
            <el-table-column prop="vehiclePlateNumber" label="车辆" />
            <el-table-column prop="route" label="路线" />
            <el-table-column prop="totalCapacity" label="装载量" />
            <el-table-column label="订单数量">
              <template #default="{ row }">
                {{ row.orderIds ? row.orderIds.length : 0 }} 单
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="row.status === 'COMPLETED' ? 'success' : 'primary'">
                  {{ row.status === 'COMPLETED' ? '已完成' : '配送中' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button v-if="row.status !== 'COMPLETED'" size="small" type="success" @click="completeDelivery(row.id)">
                  完成
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { deliveryApi } from '@/api'

export default {
  name: 'DeliveryView',
  data() {
    return {
      vehicles: [],
      deliveries: []
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      const [vehiclesRes, deliveriesRes] = await Promise.all([
        deliveryApi.getVehicles(),
        deliveryApi.getAll()
      ])
      this.vehicles = vehiclesRes.data || []
      this.deliveries = deliveriesRes.data || []
    },
    async createDelivery(vehicleId) {
      try {
        const res = await deliveryApi.create(vehicleId)
        this.loadData()
        const delivery = res.data
        this.$message.success(
          `配送单创建成功！车辆${delivery.vehiclePlateNumber}，` +
          `共${delivery.orderIds.length}单，` +
          `总容量${delivery.totalCapacity}`
        )
      } catch (e) {
        this.$message.error('创建失败: ' + (e.response?.data?.message || e.message))
      }
    },
    async completeDelivery(id) {
      await deliveryApi.complete(id)
      this.loadData()
      this.$message.success('配送完成')
    }
  }
}
</script>