
import React, { useState } from 'react';
import Person from '../classes/Person';
import Student from '../classes/Student';
import Teacher from '../classes/Teacher';

const PersonForm = ({ addPerson }) => {
  const [type, setType] = useState('Person');
  const [name, setName] = useState('');
  const [age, setAge] = useState('');
  const [extra, setExtra] = useState(''); // grade or subject

  const handleSubmit = (e) => {
    e.preventDefault();
    let newPerson;
    if (type === 'Person') newPerson = new Person(name, age);
    else if (type === 'Student') newPerson = new Student(name, age, extra);
    else if (type === 'Teacher') newPerson = new Teacher(name, age, extra);

    addPerson(newPerson);
    setName('');
    setAge('');
    setExtra('');
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginBottom: '20px' }}>
      <select value={type} onChange={e => setType(e.target.value)} style={{ marginRight: '10px' }}>
        <option value="Person">Person</option>
        <option value="Student">Student</option>
        <option value="Teacher">Teacher</option>
      </select>
      <input type="text" placeholder="Name" value={name} onChange={e => setName(e.target.value)} required />
      <input type="number" placeholder="Age" value={age} onChange={e => setAge(e.target.value)} required style={{ marginLeft: '10px' }} />
      {(type === 'Student' || type === 'Teacher') && (
        <input
          type="text"
          placeholder={type === 'Student' ? 'Grade' : 'Subject'}
          value={extra}
          onChange={e => setExtra(e.target.value)}
          required
          style={{ marginLeft: '10px' }}
        />
      )}
      <button type="submit" style={{ marginLeft: '10px' }}>Add {type}</button>
    </form>
  );
};

export default PersonForm;
