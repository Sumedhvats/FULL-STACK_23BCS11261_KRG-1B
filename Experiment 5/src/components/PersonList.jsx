import React from 'react';
import PersonCard from './PersonCard';

const PersonList = ({ people, removePerson }) => {
  return (
    <div className="card-container">
      {people.map((p, idx) => (
        <PersonCard key={idx} person={p} onDelete={() => removePerson(idx)} />
      ))}
    </div>
  );
};

export default PersonList;
