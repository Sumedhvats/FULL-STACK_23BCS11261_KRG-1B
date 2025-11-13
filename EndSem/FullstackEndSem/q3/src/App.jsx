import FruitList from "./components/FruitList"
function App() {
  const fruits = ['apple', 'banana', 'Orange']
  return (
    <div className="App">
      <h1>Fruits</h1>
      <FruitList fruits={fruits} />
    </div>
  )
}
export default App
