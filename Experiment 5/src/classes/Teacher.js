import Person from './Person.js';

export default class Teacher extends Person {
  constructor(name, age, subject) {
    super(name, age);
    this.subject = subject;
  }

  describe() {
    return `${this.name} is a teacher, ${this.age} years old, teaches ${this.subject}.`;
  }
}
