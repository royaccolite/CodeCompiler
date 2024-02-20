export class Template {
    code!: string;
    language!: string;
    constructor(language: string, code: string) {
      this.code = code;
      this.language = language;
    }
  }
  