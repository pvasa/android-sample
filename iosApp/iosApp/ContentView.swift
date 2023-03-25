import SwiftUI
import Model

struct ContentView: View {
    
    @ObservedObject var testData = ObservableTestData()
    
    var body: some View {
        Text(String(describing: $testData))
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}

class ObservableTestData : ObservableObject {

    @Published var value: TaskState<TestData>?

    let repo = TestRepositoryCompanion.init().getInstance()

    init() {
        fetchData()
    }

    private func fetchData() {
        repo.getTestData { (flow: Kotlinx_coroutines_coreFlow?, eror: Error?) in
            if let origin = flow {
                CFlow<TaskState<TestData>>(origin: origin).watch { (taskState: TaskState<TestData>?) in
                    self.value = taskState
                }
            }
        }
    }
}
