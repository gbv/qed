export default defineAppConfig({
  vue: {
    compilerOptions: {
      isCustomElement: (tag:string) => {
        return [
          'my-component',
        ].includes(tag)
      }
    }
  }
})