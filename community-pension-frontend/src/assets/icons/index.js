import { defineComponent, h } from 'vue';

// 这里可以导入所有SVG图标
const modules = import.meta.glob('./svg/*.svg', { eager: true });

export default defineComponent({
  name: 'SvgIcon',
  props: {
    iconClass: {
      type: String,
      required: true
    },
    className: {
      type: String,
      default: ''
    }
  },
  setup(props) {
    return () => {
      const iconName = `#icon-${props.iconClass}`;
      return h('svg', {
        class: ['svg-icon', props.className],
        'aria-hidden': true
      }, [
        h('use', {
          'xlink:href': iconName
        })
      ]);
    };
  }
}); 