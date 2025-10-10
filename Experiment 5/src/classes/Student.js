import Person from './Person.js';

export default class Student extends Person {
  constructor(name, age, grade) {
    super(name, age);
    this.grade = grade;
  }

  describe() {
    return `${this.name} is a student, ${this.age} years old, studying in grade ${this.grade}.`;
  }
}
