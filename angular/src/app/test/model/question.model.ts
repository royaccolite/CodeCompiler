import { Template } from "./template.model";
import { TestCase } from "./testCase.model";
export class Question {
  id!: number;
  title!: string;
  description!: string;
  templates!: Template[];
  testCases!:TestCase[]
  constructor(
    id: number,
    title: string,
    description: string,
    templates: Template[],
    testCases:TestCase[],
  ) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.templates = templates;
    this.testCases=testCases;
  }
}
