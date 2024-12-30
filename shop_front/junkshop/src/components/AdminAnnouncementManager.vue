<template>
  <div class="announcement-editor">
    <div class="toolbar">
      <el-tooltip content="添加粗体文本" placement="top">
        <el-button @click="insertMarkdown('**', '**')" type="default">B</el-button>
      </el-tooltip>
      
      <el-tooltip content="添加斜体文本" placement="top">
        <el-button @click="insertMarkdown('*', '*')" type="default"><i>I</i></el-button>
      </el-tooltip>
      
      <el-tooltip content="添加删除线" placement="top">
        <el-button @click="insertMarkdown('~~', '~~')" type="default"><del>S</del></el-button>
      </el-tooltip>

      <el-divider direction="vertical" />
      <el-tooltip content="一级标题" placement="top">
        <el-button @click="insertMarkdown('# ', '')" type="default">H1</el-button>
      </el-tooltip>
      
      <el-tooltip content="二级标题" placement="top">
        <el-button @click="insertMarkdown('## ', '')" type="default">H2</el-button>
      </el-tooltip>
      
      <el-tooltip content="三级标题" placement="top">
        <el-button @click="insertMarkdown('### ', '')" type="default">H3</el-button>
      </el-tooltip>

      <el-divider direction="vertical" />
      <el-tooltip content="无序列表" placement="top">
        <el-button @click="insertMarkdown('- ', '')" type="default">•</el-button>
      </el-tooltip>
      
      <el-tooltip content="有序列表" placement="top">
        <el-button @click="insertMarkdown('1. ', '')" type="default">1.</el-button>
      </el-tooltip>
      
      <el-tooltip content="任务列表" placement="top">
        <el-button @click="insertMarkdown('- [ ] ', '')" type="default">☐</el-button>
      </el-tooltip>

      <el-divider direction="vertical" />
      <el-tooltip content="引用文本" placement="top">
        <el-button @click="insertMarkdown('> ', '')" type="default">""</el-button>
      </el-tooltip>
      
      <el-tooltip content="添加分割线" placement="top">
        <el-button @click="insertMarkdown('\n---\n', '')" type="default">—</el-button>
      </el-tooltip>

      <el-divider direction="vertical" />
      <el-tooltip content="行内代码" placement="top">
        <el-button @click="insertMarkdown('`', '`')" type="default">`</el-button>
      </el-tooltip>
      
      <el-tooltip content="代码块" placement="top">
        <el-button @click="insertMarkdown('```\n', '\n```')" type="default">{}</el-button>
      </el-tooltip>

      <el-divider direction="vertical" />
      <el-tooltip content="添加链接" placement="top">
        <el-button @click="insertMarkdown('[', '](链接地址)')" type="default">🔗</el-button>
      </el-tooltip>
      
      <el-tooltip content="添加图片" placement="top">
        <el-button @click="insertMarkdown('![图片描述](', ')')" type="default">🖼</el-button>
      </el-tooltip>

      <el-divider direction="vertical" />
      <el-tooltip content="添加表格" placement="top">
        <el-button @click="insertTable" type="default">📊</el-button>
      </el-tooltip>
    </div>
    <div class="editor-container">
      <div class="input-section">
        <div class="section-title">编辑</div>
        <el-input
          type="textarea"
          v-model="markdownInput"
          :rows="12"
          placeholder="输入公告内容(支持markdown格式)"
          @keydown.tab.prevent="handleTab"
          ref="textareaRef"
          resize="none"
        />
      </div>
      <div class="preview-section">
        <div class="section-title">预览</div>
        <div class="preview-content">
          <vue-markdown 
            :source="markdownInput" 
            :html="true" 
            :breaks="true" 
            :linkify="true"
            class="custom-markdown"
          ></vue-markdown>
        </div>
      </div>
    </div>
    <el-button type="primary" class="submit-btn" @click="submitAnnouncement">发布公告</el-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { ElMessage } from "element-plus";
import VueMarkdown from 'vue3-markdown-it';

const markdownInput = ref("");
const textareaRef = ref(null);

// 处理 Tab 键
const handleTab = (e) => {
  const textarea = textareaRef.value;
  const start = textarea.selectionStart;
  const end = textarea.selectionEnd;
  
  markdownInput.value = 
    markdownInput.value.substring(0, start) + 
    "    " + 
    markdownInput.value.substring(end);
  
  // 重新设置光标位置
  setTimeout(() => {
    textarea.focus();
    textarea.setSelectionRange(start + 4, start + 4);
  }, 0);
};

const submitAnnouncement = async () => {
  try {
    const res = await axios.put("http://localhost:8080/changeContent", {
      content: markdownInput.value
    }, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    if (res.data === "success") {
      ElMessage.success("发布成功！");
    } else {
      ElMessage.error("发布失败！");
    }
  } catch (error) {
    console.error("发布公告失败", error);
    if (error.response) {
      ElMessage.error(`发布失败：${error.response.status} - ${error.response.data}`);
    } else {
      ElMessage.error("发布失败：" + error.message);
    }
  }
};

const getAnnouncement = async () => {
  try {
    const response = await axios.get("http://localhost:8080/getContent");
    markdownInput.value = response.data;
  } catch (error) {
    console.error("获取公告失败", error);
    ElMessage.error("获取公告失败" + error.message);
  }
};

// 添加插入表格的方法
const insertTable = () => {
  const tableTemplate = `
| 表头1 | 表头2 | 表头3 |
|-------|-------|-------|
| 内容1 | 内容2 | 内容3 |
| 内容4 | 内容5 | 内容6 |
`;
  const textarea = textareaRef.value.$el.querySelector('textarea');
  const start = textarea.selectionStart;
  const before = markdownInput.value.substring(0, start);
  const after = markdownInput.value.substring(start);
  
  markdownInput.value = before + tableTemplate + after;
  
  // 将光标移动到表格后面
  setTimeout(() => {
    textarea.focus();
    const newPosition = start + tableTemplate.length;
    textarea.setSelectionRange(newPosition, newPosition);
  }, 0);
};

onMounted(getAnnouncement);
</script>

<style scoped>
.announcement-editor {
  padding: 20px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
}

.toolbar {
  margin-bottom: 10px;
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.toolbar :deep(.el-button) {
  padding: 8px 12px;
  font-weight: bold;
}

.toolbar :deep(.el-divider--vertical) {
  margin: 0 4px;
}

.editor-container {
  display: flex;
  gap: 20px;
  height: 50vh;
}

.input-section, .preview-section {
  width: 40vw;
  display: flex;
  flex-direction: column;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.section-title {
  padding: 10px;
  font-weight: bold;
  border-bottom: 1px solid #ddd;
  background-color: #f9f9f9;
}

.input-section :deep(.el-textarea__inner) {
  border: none;
  height: 100% !important;
  resize: none;
  border-radius: 0;
  padding: 10px;
  font-family: monospace;
}

.preview-content {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  background-color: #fff;
}

:deep(.custom-markdown) {
  color: #333 !important;
}

:deep(.custom-markdown *) {
  color: #333 !important;
}

:deep(.custom-markdown a) {
  color: #0366d6 !important;
}

:deep(.custom-markdown pre),
:deep(.custom-markdown code) {
  color: #333 !important;
  background-color: #f6f8fa;
}

:deep(.custom-markdown blockquote) {
  color: #333 !important;
  border-left: 4px solid #ddd;
  padding-left: 1em;
  margin: 1em 0;
}

:deep(.custom-markdown table) {
  border-collapse: collapse;
  width: 100%;
  margin: 1em 0;
}

:deep(.custom-markdown table th),
:deep(.custom-markdown table td) {
  border: 1px solid #ddd;
  padding: 6px 13px;
  color: #333 !important;
}

:deep(.custom-markdown del) {
  color: #666 !important;
}

.submit-btn {
  width: calc(80vw + 20px);
  margin-top: 20px;
}
</style>
