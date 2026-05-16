<template>
  <div class="tasks">
    <el-card shadow="hover">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>采摘任务</span>
          <el-button type="primary" @click="generateTasks">生成任务</el-button>
        </div>
      </template>
      
      <el-table :data="tasks" border>
        <el-table-column prop="productName" label="产品" />
        <el-table-column prop="batchRegion" label="区域" />
        <el-table-column prop="maturityLevel" label="成熟度">
          <template #default="{ row }">
            <el-progress :percentage="row.maturityLevel" :status="row.maturityLevel >= 100 ? 'success' : ''" />
          </template>
        </el-table-column>
        <el-table-column prop="targetQuantity" label="目标数量" />
        <el-table-column prop="actualQuantity" label="实际数量" />
        <el-table-column prop="assignee" label="负责人" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getTaskStatusType(row.status)">{{ getTaskStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" size="small" type="primary" @click="assignTask(row)">
              分配
            </el-button>
            <el-button v-if="row.status === 'ASSIGNED'" size="small" type="success" @click="completeTask(row)">
              完成
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="assignDialogVisible" title="分配任务" width="400px">
      <el-form label-width="80px">
        <el-form-item label="负责人">
          <el-input v-model="assignee" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAssignment">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="completeDialogVisible" title="完成任务" width="400px">
      <el-form label-width="80px">
        <el-form-item label="实际数量">
          <el-input-number v-model="actualQuantity" :min="0" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCompletion">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { taskApi } from '@/api'

export default {
  name: 'TaskView',
  data() {
    return {
      tasks: [],
      assignDialogVisible: false,
      completeDialogVisible: false,
      currentTaskId: null,
      assignee: '',
      actualQuantity: 0
    }
  },
  mounted() {
    this.loadTasks()
  },
  methods: {
    async loadTasks() {
      const res = await taskApi.getAll()
      this.tasks = res.data || []
    },
    async generateTasks() {
      const res = await taskApi.generate()
      this.loadTasks()
      const tasks = res.data || []
      if (tasks.length === 0) {
        this.$message.info('没有符合条件的任务，请确保：\n1. 存在已锁定的订单\n2. 对应批次的成熟度>=10%')
      } else {
        this.$message.success(`成功生成 ${tasks.length} 个任务`)
      }
    },
    assignTask(row) {
      this.currentTaskId = row.id
      this.assignee = ''
      this.assignDialogVisible = true
    },
    async saveAssignment() {
      await taskApi.assign(this.currentTaskId, this.assignee)
      this.assignDialogVisible = false
      this.loadTasks()
      this.$message.success('分配成功')
    },
    completeTask(row) {
      this.currentTaskId = row.id
      this.actualQuantity = row.targetQuantity
      this.completeDialogVisible = true
    },
    async saveCompletion() {
      await taskApi.complete(this.currentTaskId, this.actualQuantity)
      this.completeDialogVisible = false
      this.loadTasks()
      this.$message.success('完成成功')
    },
    getTaskStatusType(status) {
      const map = { 'PENDING': 'info', 'ASSIGNED': 'primary', 'IN_PROGRESS': 'warning', 'COMPLETED': 'success', 'CANCELLED': 'info' }
      return map[status] || ''
    },
    getTaskStatusText(status) {
      const map = { 'PENDING': '待分配', 'ASSIGNED': '已分配', 'IN_PROGRESS': '进行中', 'COMPLETED': '已完成', 'CANCELLED': '已取消' }
      return map[status] || status
    }
  }
}
</script>