import React, { useState } from 'react';
import Person from './classes/Person';
import Student from './classes/Student';
import Teacher from './classes/Teacher';
import PersonList from './components/PersonList';
import PersonForm from './components/PersonForm';
import './index.css';

function App() {
  const [people, setPeople] = useState([
    new Person('Alice', 30),
    new Student('Bob', 16, '10th'),
    new Teacher('Mr. Smith', 45, 'Math'),
  ]);

  const addPerson = (p) => setPeople([...people, p]);
  const removePerson = (index) => setPeople(people.filter((_, i) => i !== index));

  return (
    <div className="app">
      <h1>Interactive OOP Demo</h1>
      <PersonForm addPerson={addPerson} />
      <PersonList people={people} removePerson={removePerson} />
    </div>
  );
}

export default App;
